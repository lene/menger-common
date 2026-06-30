package menger.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MaterialSuite extends AnyFlatSpec with Matchers:

  "Cauchy coefficients" should "return (ior, 0) when Abbe number is zero" in {
    val (a, b) = Material.cauchyCoefficients(1.5f, 0.0f)
    a shouldBe 1.5f
    b shouldBe 0.0f
  }

  it should "return (ior, 0) when Abbe number is negative" in {
    val (a, b) = Material.cauchyCoefficients(1.5f, -1.0f)
    a shouldBe 1.5f
    b shouldBe 0.0f
  }

  it should "produce correct A and B for crown glass (n_d=1.5, V_d=59)" in {
    val (a, b) = Material.cauchyCoefficients(1.5f, 59f)
    // Verified: A≈1.487, B≈4436 nm²
    a shouldBe 1.487152f +- 0.001f
    b shouldBe 4436.05f +- 0.5f
  }

  it should "produce correct A and B for diamond (n_d=2.42, V_d=33)" in {
    val (a, b) = Material.cauchyCoefficients(2.42f, 33f)
    a shouldBe 2.354764f +- 0.001f
    b shouldBe 22524.37f +- 0.5f
  }

  it should "satisfy n(λ_d) = n_d for crown glass" in {
    val (a, b) = Material.cauchyCoefficients(1.5f, 59f)
    val lambdaD = 587.6f
    val nAtD = a + b / (lambdaD * lambdaD)
    nAtD shouldBe 1.5f +- 0.001f
  }

  it should "satisfy n(λ_d) = n_d for diamond" in {
    val (a, b) = Material.cauchyCoefficients(2.42f, 33f)
    val lambdaD = 587.6f
    val nAtD = a + b / (lambdaD * lambdaD)
    nAtD shouldBe 2.42f +- 0.001f
  }

  it should "produce monotonic n(λ): shorter wavelength → higher IOR" in {
    val (a, b) = Material.cauchyCoefficients(1.5f, 59f)
    val n400 = a + b / (400f * 400f)
    val n550 = a + b / (550f * 550f)
    val n700 = a + b / (700f * 700f)
    n400 should be > n550
    n550 should be > n700
  }

  "Dispersive presets" should "have correct Abbe numbers" in {
    Material.GlassDispersive.dispersion shouldBe 59f
    Material.DiamondDispersive.dispersion shouldBe 33f
  }

  it should "have same base properties as non-dispersive presets" in {
    Material.GlassDispersive.ior shouldBe Material.Glass.ior
    Material.GlassDispersive.roughness shouldBe Material.Glass.roughness
    Material.GlassDispersive.metallic shouldBe Material.Glass.metallic

    Material.DiamondDispersive.ior shouldBe Material.Diamond.ior
    Material.DiamondDispersive.roughness shouldBe Material.Diamond.roughness
    Material.DiamondDispersive.metallic shouldBe Material.Diamond.metallic
  }

  "Dispersion field" should "default to 0 (no dispersion)" in {
    Material(Color(1f,1f,1f,1f)).dispersion shouldBe 0.0f
    Material.Glass.dispersion shouldBe 0.0f
    Material.Diamond.dispersion shouldBe 0.0f
  }

  "fromName" should "resolve dispersive presets" in {
    import scala.jdk.OptionConverters._
    Material.fromName("glass-dispersive").toScala.get.dispersion shouldBe 59f
    Material.fromName("diamond-dispersive").toScala.get.dispersion shouldBe 33f
  }
