package menger.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ColorMaterialSuite extends AnyFlatSpec with Matchers:

  private val Tolerance = 1e-6f
  private val Red = Color(1.0f, 0.0f, 0.0f)
  private val Green = Color(0.0f, 1.0f, 0.0f)
  private val Blue = Color(0.0f, 0.0f, 1.0f)

  private def assertColor(actual: Color, expected: Color): Unit =
    actual.r shouldBe expected.r +- Tolerance
    actual.g shouldBe expected.g +- Tolerance
    actual.b shouldBe expected.b +- Tolerance
    actual.a shouldBe expected.a +- Tolerance

  "Color" should "default alpha to fully opaque and expose array forms" in:
    val color = Color(0.25f, 0.5f, 0.75f)

    color.a shouldBe 1.0f
    color.toRGBArray.toSeq shouldBe Seq(0.25f, 0.5f, 0.75f)
    color.toRGBAArray.toSeq shouldBe Seq(0.25f, 0.5f, 0.75f, 1.0f)

  it should "construct from integer RGB and RGBA components" in:
    assertColor(Color.fromRGB(255, 128, 0), Color(1.0f, 128f / 255f, 0.0f, 1.0f))
    assertColor(Color.fromRGBA(255, 128, 0, 64), Color(1.0f, 128f / 255f, 0.0f, 64f / 255f))

  it should "parse six and eight digit hex colors" in:
    assertColor(Color.fromHex("#336699"), Color(0x33 / 255f, 0x66 / 255f, 0x99 / 255f, 1.0f))
    assertColor(
      Color.fromHex("33669980"),
      Color(0x33 / 255f, 0x66 / 255f, 0x99 / 255f, 0x80 / 255f)
    )

  it should "reject out of range float components and malformed hex strings" in:
    an[IllegalArgumentException] shouldBe thrownBy(Color(-0.01f, 0.0f, 0.0f))
    an[IllegalArgumentException] shouldBe thrownBy(Color(0.0f, 1.01f, 0.0f))
    an[IllegalArgumentException] shouldBe thrownBy(Color(0.0f, 0.0f, 1.01f))
    an[IllegalArgumentException] shouldBe thrownBy(Color(0.0f, 0.0f, 0.0f, -0.01f))
    an[IllegalArgumentException] shouldBe thrownBy(Color.fromHex("#12345"))

  it should "define the shared light gray constant" in:
    assertColor(Color.LIGHT_GRAY, Color(200f / 255f, 200f / 255f, 200f / 255f, 1.0f))

  "Material" should "use documented constructor defaults" in:
    val material = Material(Red)

    material.color shouldBe Red
    material.ior shouldBe 1.0f
    material.roughness shouldBe 0.5f
    material.metallic shouldBe 0.0f
    material.specular shouldBe 0.5f
    material.emission shouldBe 0.0f
    material.filmThickness shouldBe 0.0f
    material.baseColorTexture shouldBe -1
    material.normalTexture shouldBe -1
    material.roughnessTexture shouldBe -1

  it should "create matte plastic metal and glass materials from custom colors" in:
    Material.matte(Red) shouldBe Material(Red, ior = 1.0f, roughness = 1.0f, specular = 0.0f)
    Material.plastic(Green) shouldBe Material(Green, ior = 1.5f, roughness = 0.3f, specular = 0.5f)
    Material.metal(Blue) shouldBe Material(
      Blue, ior = 1.0f, roughness = 0.1f, metallic = 1.0f, specular = 1.0f
    )
    Material.glass(Red) shouldBe Material(
      Red.copy(a = 0.02f), ior = 1.5f, roughness = 0.0f, specular = 1.0f
    )

  it should "expose physically meaningful built-in presets" in:
    Material.Glass.color.a shouldBe 0.02f
    Material.Water.ior shouldBe Const.iorWater
    Material.Diamond.ior shouldBe Const.iorDiamond
    Material.Chrome.metallic shouldBe 1.0f
    Material.Gold.color.r shouldBe 1.0f
    Material.Copper.roughness shouldBe 0.2f
    Material.Film.filmThickness shouldBe 500.0f
    Material.Parchment.ior shouldBe 1.0f
    Material.Plastic shouldBe Material.plastic(Color(1.0f, 1.0f, 1.0f, 1.0f))
    Material.Matte shouldBe Material.matte(Color(1.0f, 1.0f, 1.0f, 1.0f))

  it should "look up presets by name case-insensitively" in:
    Material.fromName("GLASS").get shouldBe Material.Glass
    Material.fromName("water").get shouldBe Material.Water
    Material.fromName("Diamond").get shouldBe Material.Diamond
    Material.fromName("chrome").get shouldBe Material.Chrome
    Material.fromName("gold").get shouldBe Material.Gold
    Material.fromName("copper").get shouldBe Material.Copper
    Material.fromName("film").get shouldBe Material.Film
    Material.fromName("parchment").get shouldBe Material.Parchment
    Material.fromName("metal").get shouldBe Material.metal(Color(1.0f, 1.0f, 1.0f, 1.0f))
    Material.fromName("plastic").get shouldBe Material.Plastic
    Material.fromName("matte").get shouldBe Material.Matte

  it should "return empty lookups for unknown presets and list known names" in:
    Material.fromName("unknown").isEmpty shouldBe true
    Material.presetNames.contains("glass") shouldBe true
    Material.presetNames.contains("matte") shouldBe true
    Material.presetNames.size shouldBe 11
