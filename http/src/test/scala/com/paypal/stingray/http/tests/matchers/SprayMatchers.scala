package com.paypal.stingray.http.tests.matchers

import org.specs2.matcher.{Matcher, Expectable, MatchResult}
import spray.http._
import spray.http.HttpEntity._
import com.paypal.stingray.http.resource.{AbstractResource, ResourceDriver}
import com.paypal.stingray.common.option._
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Try

/**
 * Utility match cases for testing [[com.paypal.stingray.http.resource.AbstractResource]]
 * and [[com.paypal.stingray.http.resource.ResourceService]] implementations
 */
trait SprayMatchers {

  /** Resource driver to use for requests */
  lazy val driver: ResourceDriver = new ResourceDriver{}
  
  /** Default timeout for SprayMatcher responses. Default of 2 seconds; override if necessary. */
  lazy val sprayMatcherAwaitDuration: Duration = 2.seconds

  /**
   * Requires that a run request must have a certain response code
   * @param req the request to run the request to run
   * @param pathParts the path
   * @param code the response code required
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInCodeGivenData[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                        pathParts: Map[String, String],
                                                                        code: StatusCode) =
    new ResponseHasCode[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, code)

  /**
   * Requires that a run request must have a certain response body
   * @param req the request to run
   * @param pathParts the path
   * @param body the body required
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInBodyGivenData[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                        pathParts: Map[String, String],
                                                                        body: HttpEntity) =
    new ResponseHasBody[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, body)

  /**
   * Requires that a run request must have a certain response body
   * @param req the request to run
   * @param pathParts the path
   * @param body the body required
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInBodyStringGivenData[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                              pathParts: Map[String, String],
                                                                              body: String) =
    new ResponseHasBodyString[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, body)

  /**
   * Requires that a run request must have a response body that passes a given comparison function
   * @param req the request to run
   * @param pathParts the path
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInBodyLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                   pathParts: Map[String, String])
                                                                  (f: HttpEntity => MatchResult[Any]) =
    new ResponseHasBodyLike[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, f)

  /**
   * Requires that a run request must have a response body that passes a given comparison function
   * @param req the request to run
   * @param pathParts the path
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInBodyStringLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                         pathParts: Map[String, String])
                                                                        (f: String => MatchResult[Any]) =
    new ResponseHasBodyStringLike[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, f)

  /**
   * Requires that a run request must have a certain response code and a response body that passes a given function
   * @param req the request to run
   * @param pathParts the path
   * @param code the response code required
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInCodeAndBodyLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                          pathParts: Map[String, String],
                                                                          code: StatusCode)
                                                                         (f: HttpEntity => MatchResult[Any]) =
    new ResponseHasCodeAndBodyLike[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, code, f)

  /**
   * Requires that a run request must have a certain response code and a response body that passes a given function
   * @param req the request to run
   * @param pathParts the path
   * @param code the response code required
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInCodeAndBodyStringLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                                pathParts: Map[String, String],
                                                                                code: StatusCode)
                                                                               (f: String => MatchResult[Any]) =
    new ResponseHasCodeAndBodyStringLike[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, code, f)

  /**
   * Requires that a run request must have a given header in its response
   * @param req the request to run
   * @param pathParts the path
   * @param header the header
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInResponseWithHeaderContaining[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                                       pathParts: Map[String, String],
                                                                                       header: HttpHeader) =
    new ResponseHasHeaderContainingValue[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, header)

  /**
   * Requires that a run request must have a given header and header value in its response
   * @param req the request to run
   * @param pathParts the path
   * @param header the header
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInResponseWithNonEmptyHeader[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                                     pathParts: Map[String, String],
                                                                                     header: String) =
    new ResponseHasNonEmptyHeader[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, header)

  /**
   * Requires that a run request must have a given `Content-Type` header in its response
   * @param req the request to run
   * @param pathParts the path
   * @param cType the content type
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   * @return an object that can yield a [[org.specs2.matcher.MatchResult]]
   */
  def resultInContentType[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                      pathParts: Map[String, String],
                                                                      cType: ContentType) =
    new ResponseHasContentType[ParsedRequest, AuthInfo, PostBody, PutBody](req, pathParts, cType)

  /**
   * Requires that a run request must have a certain response code
   * @param req the request to run the request to run
   * @param pathParts the path
   * @param code the response code required
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasCode[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                    pathParts: Map[String, String],
                                                                    code: StatusCode)
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      result(
        code == resp.status,
        s"Response has code: ${code.intValue}",
        s"Response has code: ${resp.status.intValue} (body: ${Try(resp.entity.asString).getOrElse("not available")}) expected: $code",
        r
      )
    }
  }

  /**
   * Requires that a run request must have a certain response body
   * @param req the request to run
   * @param pathParts the path
   * @param body the body required
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasBody[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                    pathParts: Map[String, String],
                                                                    body: HttpEntity)
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      result(
        body == resp.entity,
        "Expected response body found",
        s"response body: ${resp.entity.asString} is not equal to $body",
        r
      )
    }
  }

  /**
   * Requires that a run request must have a certain response body
   * @param req the request to run
   * @param pathParts the path
   * @param body the body required
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasBodyString[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                          pathParts: Map[String, String],
                                                                          body: String)
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      result(
        body == resp.entity.asString,
        "Expected response body found",
        s"response body: ${resp.entity.asString} is not equal to $body",
        r
      )
    }
  }

  /**
   * Requires that a run request must have a certain response code and a response body that passes a given function
   * @param req the request to run
   * @param pathParts the path
   * @param code the response code required
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasCodeAndBodyLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                               pathParts: Map[String, String],
                                                                               code: StatusCode,
                                                                               f: HttpEntity => MatchResult[Any])
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      val matchResult = f(resp.entity)
      result(
        code == resp.status && matchResult.isSuccess,
        "success",
        if (code != resp.status)
          s"Response has code: ${resp.status.intValue} body: ${resp.entity.asString} expected: $code"
        else
          matchResult.message,
        r
      )
    }
  }

  /**
   * Requires that a run request must have a certain response code and a response body that passes a given function
   * @param req the request to run
   * @param pathParts the path
   * @param code the response code required
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasCodeAndBodyStringLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                                     pathParts: Map[String, String],
                                                                                     code: StatusCode,
                                                                                     f: String => MatchResult[Any])
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      val matchResult = f(resp.entity.asString)
      result(
        code == resp.status && matchResult.isSuccess,
        "success",
        if (code != resp.status)
          s"Response has code: ${resp.status.intValue} body: ${resp.entity.asString} expected: $code"
        else
          matchResult.message,
        r
      )
    }
  }

  /**
   * Requires that a run request must have a response body that passes a given comparison function
   * @param req the request to run
   * @param pathParts the path
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasBodyLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                        pathParts: Map[String, String],
                                                                        f: HttpEntity => MatchResult[Any])
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      val matchResult = f(resp.entity)
      result(
        matchResult.isSuccess,
        "success",
        matchResult.message,
        r
      )
    }
  }

  /**
   * Requires that a run request must have a response body that passes a given comparison function
   * @param req the request to run
   * @param pathParts the path
   * @param f the comparison function
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasBodyStringLike[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                              pathParts: Map[String, String],
                                                                              f: String => MatchResult[Any])
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      val matchResult = f(resp.entity.asString)
      result(
        matchResult.isSuccess,
        "success",
        matchResult.message,
        r
      )
    }
  }

  class ResponseHasHeaderContainingValue[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                                     pathParts: Map[String, String],
                                                                                     header: HttpHeader)
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      val hdr = resp.headers.find(_.lowercaseName == header.lowercaseName)
      result(
        hdr.exists(_ == header),
        s"header ${header.lowercaseName} has expected value: ${header.value}",
        hdr.map (v => s"header ${header.lowercaseName} exists but has value: $v expected: ${header.value}")
          .getOrElse(s"header ${header.lowercaseName} does not exist"),
        r
      )
    }
  }

  /**
   * Requires that a run request must have a given header and header value in its response
   * @param req the request to run
   * @param pathParts the path
   * @param header the header
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasNonEmptyHeader[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                              pathParts: Map[String, String],
                                                                              header: String)
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      val hdr = resp.headers.find(_.lowercaseName == header)
      result(
        hdr.exists(_.value.trim != ""),
        s"header $header exists and is non-empty",
        hdr.map(_ => s"header $header exists but is empty")
          .getOrElse(s"header $header does not exist"),
        r
      )
    }
  }

  /**
   * Requires that a run request must have a given `Content-Type` header in its response
   * @param req the request to run
   * @param pathParts the path
   * @param cType the content type
   * @tparam ParsedRequest the parsed request type
   * @tparam AuthInfo the auth container type
   * @tparam PostBody the parsed POST body type
   * @tparam PutBody the parsed PUT body type
   */
  class ResponseHasContentType[ParsedRequest, AuthInfo, PostBody, PutBody](req: HttpRequest,
                                                                           pathParts: Map[String, String],
                                                                           cType: ContentType)
    extends Matcher[AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]] {

    override def apply[S <: AbstractResource[ParsedRequest, AuthInfo, PostBody, PutBody]](r: Expectable[S]): MatchResult[S] = {
      val resp = Await.result(driver.serveSync(req, r.value, pathParts), sprayMatcherAwaitDuration)
      val resultCType = resp.entity.some collect {
        case NonEmpty(c, _) => c
      }
      result(
        resultCType.exists(_ == cType),
        s"content type ${cType.toString()} found",
        resultCType.map(ct => s"content type is: $ct expected: $cType")
          .getOrElse("content type header not found"),
        r
      )
    }
  }
}