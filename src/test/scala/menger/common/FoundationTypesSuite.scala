package menger.common

import menger.common.InputEvent.KeyPress
import menger.common.InputEvent.KeyRelease
import menger.common.InputEvent.MouseDown
import menger.common.InputEvent.MouseDrag
import menger.common.InputEvent.MouseUp
import menger.common.InputEvent.ScrollEvent
import menger.common.Light.Area
import menger.common.Light.Directional
import menger.common.Light.Point
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FoundationTypesSuite extends AnyFlatSpec with Matchers:

  private val Tolerance = 1e-5f

  "RenderConfig" should "provide safe defaults and high quality settings" in:
    RenderLimits.MaxRayDepth shouldBe 5
    RenderConfig.Default shouldBe RenderConfig()
    RenderConfig.Default.maxRayDepth shouldBe RenderLimits.MaxRayDepth
    RenderConfig.HighQuality.shadows shouldBe true
    RenderConfig.HighQuality.antialiasing shouldBe true
    RenderConfig.HighQuality.aaMaxDepth shouldBe 3
    RenderConfig.HighQuality.aaThreshold shouldBe 0.05f

  it should "reject invalid anti-aliasing and ray-depth bounds" in:
    an[IllegalArgumentException] shouldBe thrownBy(RenderConfig(aaMaxDepth = 0))
    an[IllegalArgumentException] shouldBe thrownBy(RenderConfig(aaMaxDepth = 5))
    an[IllegalArgumentException] shouldBe thrownBy(RenderConfig(aaThreshold = -0.01f))
    an[IllegalArgumentException] shouldBe thrownBy(RenderConfig(aaThreshold = 1.01f))
    an[IllegalArgumentException] shouldBe thrownBy(RenderConfig(maxRayDepth = 0))
    an[IllegalArgumentException] shouldBe thrownBy(RenderConfig(maxRayDepth = 6))

  "CausticsConfig" should "provide disabled default and high quality presets" in:
    CausticsConfig.Disabled shouldBe CausticsConfig()
    CausticsConfig.Default.enabled shouldBe true
    CausticsConfig.HighQuality.enabled shouldBe true
    CausticsConfig.HighQuality.photonsPerIteration shouldBe 500000
    CausticsConfig.HighQuality.iterations shouldBe 20
    CausticsConfig.HighQuality.alpha shouldBe 0.8f

  it should "reject invalid caustics bounds" in:
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(photonsPerIteration = 0))
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(photonsPerIteration = 10000001))
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(iterations = 0))
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(iterations = 1001))
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(initialRadius = 0.0f))
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(initialRadius = 10.1f))
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(alpha = 0.0f))
    an[IllegalArgumentException] shouldBe thrownBy(CausticsConfig(alpha = 1.0f))

  "Light" should "tag directional point and area lights with their type" in:
    val white = Color(1.0f, 1.0f, 1.0f)
    val direction = Vector[3](1.0f, -1.0f, 0.0f)
    val position = Vector[3](1.0f, 2.0f, 3.0f)
    val normal = Vector[3](0.0f, 1.0f, 0.0f)

    Directional(direction).lightType shouldBe LightType.Directional
    Directional(direction).color shouldBe white
    Directional(direction).intensity shouldBe 1.0f
    Point(position).lightType shouldBe LightType.Point
    Point(position).color shouldBe white
    Area(position, normal, radius = 2.0f).lightType shouldBe LightType.Area
    Area(position, normal, radius = 2.0f).shape shouldBe AreaLightShape.Disk
    AreaLightShape.Disk.id shouldBe 0

  it should "reject invalid intensity radius and shadow sample bounds" in:
    val vector = Vector[3](0.0f, 1.0f, 0.0f)

    an[IllegalArgumentException] shouldBe thrownBy(Directional(vector, intensity = -0.1f))
    an[IllegalArgumentException] shouldBe thrownBy(Point(vector, intensity = -0.1f))
    an[IllegalArgumentException] shouldBe thrownBy(Area(vector, vector, radius = 0.0f))
    an[IllegalArgumentException] shouldBe thrownBy(
      Area(vector, vector, radius = 1.0f, intensity = -0.1f)
    )
    an[IllegalArgumentException] shouldBe thrownBy(
      Area(vector, vector, radius = 1.0f, shadowSamples = 0)
    )
    an[IllegalArgumentException] shouldBe thrownBy(
      Area(vector, vector, radius = 1.0f, shadowSamples = 17)
    )

  "Input domain types" should "model keyboard and mouse events" in:
    val modifiers = ModifierState().withCtrl(true).withAlt(true).withShift(true)
    val coords = ScreenCoords(12, 34)

    modifiers shouldBe ModifierState(ctrl = true, alt = true, shift = true)
    KeyPress(Key.C, modifiers).key shouldBe Key.C
    KeyRelease(Key.Unknown(42), modifiers).modifiers shouldBe modifiers
    MouseDown(coords, MouseButton.Left, pointer = 1).position shouldBe coords
    MouseUp(coords, MouseButton.Right, pointer = 2).button shouldBe MouseButton.Right
    MouseDrag(coords, pointer = 3, button = MouseButton.Middle).pointer shouldBe 3
    MouseButton.Unknown(99).code shouldBe 99
    ScrollEvent(1.0f, -2.0f).amountY shouldBe -2.0f

  "Config and scene specs" should "expose defaults and helper predicates" in:
    val fogColor = Color(0.8f, 0.8f, 0.9f)
    val solid = PlaneColorSpec(Color(1.0f, 0.0f, 0.0f), None)
    val checkered = PlaneColorSpec(Color(1.0f, 0.0f, 0.0f), Some(Color(0.0f, 0.0f, 1.0f)))

    FogConfig(0.2f).color shouldBe fogColor
    FogSpec(0.3f).color shouldBe fogColor
    PlaneSpec(Axis.X, positive = true, value = 1.0f).axis shouldBe Axis.X
    solid.isSolid shouldBe true
    solid.isCheckered shouldBe false
    checkered.isSolid shouldBe false
    checkered.isCheckered shouldBe true

  it should "provide orbit defaults from shared input constants" in:
    val orbit = OrbitConfig()

    orbit.orbitSensitivity shouldBe Const.Input.defaultZoomSensitivity
    orbit.panSensitivity shouldBe Const.Input.defaultPanSensitivity
    orbit.zoomSensitivity shouldBe Const.Input.zoomSensitivity
    orbit.minDistance shouldBe Const.Input.defaultMinDistance
    orbit.maxDistance shouldBe Const.Input.defaultMaxDistance
    orbit.minElevation shouldBe Const.Input.defaultMinElevation
    orbit.maxElevation shouldBe Const.Input.defaultMaxElevation

  "ProfilingConfig" should "switch between disabled and threshold modes" in:
    ProfilingConfig.disabled.isEnabled shouldBe false
    ProfilingConfig.disabled.threshold shouldBe Int.MaxValue
    ProfilingConfig.enabled(25).isEnabled shouldBe true
    ProfilingConfig.enabled(25).threshold shouldBe 25

  "Vector3 helpers" should "convert between arrays and coordinate accessors" in:
    val vector = Vector3.fromArray(Array(1.0f, 2.0f, 3.0f))

    vector.x shouldBe 1.0f
    vector.y shouldBe 2.0f
    vector.z shouldBe 3.0f
    vector.toArray.toSeq shouldBe Seq(1.0f, 2.0f, 3.0f)
    an[IllegalArgumentException] shouldBe thrownBy(Vector3.fromArray(Array(1.0f, 2.0f)))

  "Const" should "convert angles and expose rendering constants" in:
    Const.degreesToRadians(180.0f) shouldBe math.Pi.toFloat +- Tolerance
    Const.radiansToDegrees(math.Pi.toFloat) shouldBe 180.0f +- Tolerance
    Const.Renderer.transformMatrixSize shouldBe 12
    Const.Engine.trianglesPerCube shouldBe 12
    Const.Display.depthBits shouldBe 16
    Const.Geometry.verticesPerFace shouldBe 4

  "ImageSize" should "store dimensions" in:
    ImageSize(1920, 1080).width shouldBe 1920
    ImageSize(1920, 1080).height shouldBe 1080
