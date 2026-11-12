package menger.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScaleSuite extends AnyFlatSpec with Matchers:
  import Vector.Scale

  "A Scale" should "hold one factor per axis" in:
    val s = Scale[4](1f, 1f, 1f, 0.5f)
    s.dimension should be (4)
    s(3) should be (0.5f)
    s.toIndexedSeq should be (IndexedSeq(1f, 1f, 1f, 0.5f))

  it should "accept a zero factor, collapsing the axis" in:
    val s = Scale[5](1f, 1f, 1f, 1f, 0f)
    s(4) should be (0f)

  it should "reject the wrong number of factors, naming expected and actual" in:
    val e = the [IllegalArgumentException] thrownBy Scale[4](1f, 1f, 1f)
    e.getMessage should be ("requirement failed: Expected 4 elements, got 3")

  it should "reject a negative factor, naming the value and the bound" in:
    val e = the [IllegalArgumentException] thrownBy Scale[4](1f, -1f, 1f, 1f)
    e.getMessage should be ("requirement failed: Scale factor 1 must be finite and >= 0, got -1.0")

  it should "reject NaN and infinite factors, naming the value" in:
    (the [IllegalArgumentException] thrownBy Scale[2](1f, Float.NaN)).getMessage should include ("NaN")
    (the [IllegalArgumentException] thrownBy Scale[2](Float.PositiveInfinity, 1f))
      .getMessage should include ("Infinity")
    (the [IllegalArgumentException] thrownBy Scale[2](1f, Float.NegativeInfinity))
      .getMessage should include ("-Infinity")

  "Scale.identity" should "be all ones and report itself as identity" in:
    val s = Scale.identity[6]
    s.toIndexedSeq should be (IndexedSeq.fill(6)(1f))
    s.isIdentity should be (true)
    Scale[4](1f, 1f, 1f, 0.5f).isIdentity should be (false)
    Scale[4](2f, 1f, 1f, 1f).isIdentity should be (false)
    Scale[3](1f, 0f, 1f).isIdentity should be (false)

  "Applying a Scale" should "multiply a vector component-wise" in:
    val s = Scale[4](2f, 1f, 1f, 0f)
    val result = s(Vector[4](1f, 2f, 3f, 4f))
    result.toIndexedSeq should be (Vector[4](2f, 2f, 3f, 0f).toIndexedSeq)

  it should "scale each axis by its own factor, in order" in:
    val s = Scale[4](2f, 3f, 4f, 5f)
    s(Vector[4](1f, 1f, 1f, 1f)).toIndexedSeq should be (IndexedSeq(2f, 3f, 4f, 5f))

  it should "leave a vector unchanged at the identity" in:
    val v = Vector[3](1.5f, -2f, 0.25f)
    val id = Scale.identity[3]
    id(v) should be (v)

  "withLast" should "replace only the last factor" in:
    Scale.identity[5].withLast(0.25f).toIndexedSeq should be (IndexedSeq(1f, 1f, 1f, 1f, 0.25f))

  it should "work on a one-dimensional scale" in:
    Scale[1](2f).withLast(3f).toIndexedSeq should be (IndexedSeq(3f))

  it should "reject a negative factor" in:
    val e = the [IllegalArgumentException] thrownBy Scale.identity[5].withLast(-1f)
    e.getMessage should (include ("factor 4") and include ("-1.0") and include (">= 0"))

  "Element access" should "reject an out-of-range index" in:
    val s = Scale[4](1f, 1f, 1f, 1f)
    an [IllegalArgumentException] should be thrownBy s(4)
    an [IllegalArgumentException] should be thrownBy s(-1)

  "Scales" should "be equal when their factors are" in:
    Scale[2](1f, 2f) should be (Scale[2](1f, 2f))
    Scale[2](1f, 2f).hashCode should be (Scale[2](1f, 2f).hashCode)
    Scale[3](1f, 1f, 1f) should be (Scale.identity[3])
    Scale[2](1f, 2f) should not be (Scale[2](2f, 1f))

  "toString" should "list the factors like a Vector, prefixed with Scale" in:
    Scale[3](1f, 2.5f, 0f).toString should be ("Scale<1, 2.50, 0>")

  "A zero-dimensional Scale" should "be rejected" in:
    an [IllegalArgumentException] should be thrownBy Scale.identity[0]
