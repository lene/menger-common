package menger.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TransformUtilSuite extends AnyFlatSpec with Matchers:

  private val Tolerance = 1e-5f
  private val RightAngle = (math.Pi / 2.0).toFloat

  private def assertArrayClose(actual: Array[Float], expected: Seq[Float]): Unit =
    actual.length shouldBe expected.length
    actual.zip(expected).foreach { case (actualValue, expectedValue) =>
      actualValue shouldBe expectedValue +- Tolerance
    }

  "TransformUtil" should "create scale translation transforms in row-major layout" in:
    TransformUtil.createScaleTranslation(2.0f, 3.0f, 4.0f, 5.0f).toSeq shouldBe Seq(
      2.0f, 0.0f, 0.0f, 3.0f,
      0.0f, 2.0f, 0.0f, 4.0f,
      0.0f, 0.0f, 2.0f, 5.0f
    )

  it should "create identity translation and uniform scale helpers" in:
    TransformUtil.identity().toSeq shouldBe Seq(
      1.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 1.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 1.0f, 0.0f
    )
    TransformUtil.translation(1.0f, 2.0f, 3.0f).toSeq shouldBe Seq(
      1.0f, 0.0f, 0.0f, 1.0f,
      0.0f, 1.0f, 0.0f, 2.0f,
      0.0f, 0.0f, 1.0f, 3.0f
    )
    TransformUtil.uniformScale(3.0f).toSeq shouldBe Seq(
      3.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 3.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 3.0f, 0.0f
    )

  it should "rotate local position and orientation around the Y axis" in:
    val transform = TransformUtil.createYRotationScaleTranslation(
      RightAngle, scale = 2.0f, lx = 1.0f, ly = 3.0f, lz = 4.0f
    )

    assertArrayClose(transform, Seq(
      0.0f, 0.0f, 2.0f, 4.0f,
      0.0f, 2.0f, 0.0f, 3.0f,
      -2.0f, 0.0f, 0.0f, -1.0f
    ))

  it should "match scale translation for zero Euler rotation" in:
    val transform = TransformUtil.createEulerRotationScaleTranslation(
      rx = 0.0f, ry = 0.0f, rz = 0.0f,
      scale = 2.0f,
      tx = 3.0f, ty = 4.0f, tz = 5.0f
    )

    assertArrayClose(transform, TransformUtil.createScaleTranslation(2.0f, 3.0f, 4.0f, 5.0f).toSeq)

  it should "create ZYX Euler rotation matrices" in:
    val transform = TransformUtil.createEulerRotationScaleTranslation(
      rx = 0.0f, ry = 0.0f, rz = RightAngle,
      scale = 1.0f,
      tx = 0.0f, ty = 0.0f, tz = 0.0f
    )

    assertArrayClose(transform, Seq(
      0.0f, -1.0f, 0.0f, 0.0f,
      1.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 1.0f, 0.0f
    ))
