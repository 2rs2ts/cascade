package com.paypal.stingray.common.tests.string

import com.paypal.stingray.common.tests.scalacheck._
import org.specs2._
import org.scalacheck.Gen._
import org.scalacheck.Prop._
import com.paypal.stingray.common.string.RichString
import com.paypal.stingray.common.constants.ValueConstants.charsetUtf8

/**
 * Tests for [[com.paypal.stingray.common.string]]
 */
class StringSpecs extends Specification with ScalaCheck { def is = s2"""

  string.scala provides convenience methods and implicit wrappers for working with Strings

  string.getBytes should invoke implicit class RichString getBytesUTF8 method   ${GetBytes().ok}

"""

  case class GetBytes() {
    def ok = forAll(genNonEmptyAlphaNumStr) { str =>
      val bytesArray = str.getBytesUTF8
      val decoded = new String(bytesArray, charsetUtf8)
      (bytesArray must beAnInstanceOf[Array[Byte]]) and
        (str must beEqualTo(decoded))
    }
  }

}

