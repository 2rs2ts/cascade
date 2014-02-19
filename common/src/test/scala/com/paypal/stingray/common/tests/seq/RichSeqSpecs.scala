package com.paypal.stingray.common.tests.seq

import org.specs2.Specification
import org.specs2.execute.{Result => SpecsResult}
import com.paypal.stingray.common.tests.util.CommonImmutableSpecificationContext
import com.paypal.stingray.common.seq.RichSeq

/**
 * Tests for implicit [[com.paypal.stingray.common.seq.RichSeq]]
 */

class RichSeqSpecs extends Specification { def is =
  "RichSeqSpecs".title                                                                                                  ^
  """
  RichSeq is a wrapper for Seq[T]. It contains stuff that is good.
  """                                                                                                                   ^
  "RichSeq[T]#get should"                                                                                               ^
    "return Some(t) if the index was in bounds"                                                                         ! Get().inBoundsReturnsSome ^
    "return None if the index was out of bounds"                                                                        ! Get().outOfBoundsReturnsNone ^
                                                                                                                        end
  trait Context extends CommonImmutableSpecificationContext {
    protected lazy val s = Seq(1, 2, 3)
    protected lazy val wrapper = new RichSeq(s)
  }

  case class Get() extends Context {
    def inBoundsReturnsSome: SpecsResult = s.get(0) must beEqualTo(Some(s(0)))
    def outOfBoundsReturnsNone: SpecsResult = {
      (wrapper.get(-1) must beNone) and
      (wrapper.get(s.length) must beNone)
    }
  }

}