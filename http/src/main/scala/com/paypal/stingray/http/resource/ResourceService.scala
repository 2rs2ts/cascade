package com.paypal.stingray.http.resource

import spray.can.Http.GetStats
import spray.can.server.Stats
import spray.http._
import spray.http.ContentTypes
import spray.http.HttpEntity
import spray.http.StatusCodes._
import spray.routing._
import com.paypal.stingray.common.service.ServiceNameComponent
import com.paypal.stingray.common.values.{StaticValuesComponent, BuildStaticValues}
import com.paypal.stingray.common.json._
import com.paypal.stingray.http.server.StatusResponse
import scala.concurrent.ExecutionContext
import scala.util.{Try, Failure, Success}
import scala.concurrent.duration._
import akka.actor.ActorSelection
import akka.pattern.ask

/**
 * Base type for implementing resource-based services. Contains several often-used patterns, e.g. stats and status
 * endpoints, and is driven by [[com.paypal.stingray.http.resource.ResourceDriver]].
 *
 * Implementing classes should override `route` with their own Spray route logic. This should serve as the top-most
 * actor (or very close to top) in a Spray-based actor hierarchy, as it is the first point of entry into most services.
 *
 * See https://confluence.paypal.com/cnfl/display/stingray/AbstractResource%2C+ResourceDriver%2C+and+ResourceService
 * for more information.
 */
trait ResourceService
  extends HttpService
  with ResourceDriver {
  this: StaticValuesComponent
    with ServiceNameComponent =>

  /** The execution context in which Futures will be executed */
  protected implicit def ec: ExecutionContext

  /** A source of build-specific values for this service */
  protected lazy val bsvs = new BuildStaticValues(svs)

  /** The routing rules for this service */
  val route: Route

  private lazy val statusResponse = StatusResponse.getStatusResponse(bsvs, serviceName)

  private lazy val statusError = """{"status":"error"}"""

  private lazy val statusRoute: Route = path("status") { _ =>
    val statusRespJson = JsonUtil.toJson(statusResponse).getOrElse(statusError)
    complete(HttpResponse(OK, HttpEntity(ContentTypes.`application/json`, statusRespJson)))
  }

  private lazy val serverActor: ActorSelection = actorRefFactory.actorSelection("/user/IO-HTTP/listener-0")

  private lazy val statsError = """{"stats":"error"}"""

  private lazy val statsRoute: Route = path("stats") { _ =>
    (ctx: RequestContext) => {
      (serverActor ? GetStats)(1.second).mapTo[Stats].onComplete {
        case Success(stats) => {
          val statsRespJson = JsonUtil.toJson(stats).getOrElse(statsError)
          ctx.complete(HttpResponse(OK, HttpEntity(ContentTypes.`application/json`, statsRespJson)))
        }
        case Failure(t) => {
          val statsFailureJson =
            JsonUtil.toJson(Map("errors" -> List(Option(t.getMessage).getOrElse("")))).getOrElse(statsError)
          ctx.complete(
            HttpResponse(InternalServerError, HttpEntity(ContentTypes.`application/json`, statsFailureJson)))
        }
      }
    }
  }

  /** The route before sealing into `fullRoute`. This should not be overridden. */
  protected lazy val unsealedFullRoute: Route = statusRoute ~ statsRoute ~ route

  /** The route after sealing, which will be used to handle requests. This should not be overridden.*/
  lazy val fullRoute: Route = sealRoute(unsealedFullRoute)

  /**
   * Run the request on this resource, first applying a rewrite. This should not be overridden.
   * @param resource this resource
   * @param rewrite a method by which to rewrite the request
   * @tparam ParsedRequest the request after parsing
   * @tparam AuthInfo the authorization container
   * @tparam PostBody the POST body after parsing
   * @tparam PutBody the PUT body after parsing
   * @return the rewritten request execution
   */
  def serveWithRewrite[ParsedRequest, AuthInfo, PostBody, PutBody]
  (resource: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody])
  (rewrite: HttpRequest => Try[(HttpRequest, Map[String, String])]): RequestContext => Unit = { ctx: RequestContext =>
    rewrite(ctx.request).map { case (request, pathParts) =>
      serve(resource, pathParts)(ctx.copy(request = request))
    }.recover {
      case e: Throwable =>
        ctx.complete(HttpResponse(BadRequest, HttpEntity(ContentTypes.`application/json`, e.getMessage)))
    }
  }

  /**
   * Run the request on this resource. This should not be overridden.
   * @param resource this resource
   * @param pathParts the parsed path
   * @tparam ParsedRequest the request after parsing
   * @tparam AuthInfo the authorization container
   * @tparam PostBody the POST body after parsing
   * @tparam PutBody the PUT body after parsing
   * @return the request execution
   */
  def serve[ParsedRequest, AuthInfo, PostBody, PutBody]
  (resource: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody],
   pathParts: Map[String, String] = Map()): RequestContext => Unit = { ctx: RequestContext =>
    ctx.complete(serveSync(ctx.request, resource, pathParts))
  }

}