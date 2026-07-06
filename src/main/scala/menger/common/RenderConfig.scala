package menger.common

object RenderLimits:
  /** Pipeline-level ray trace depth ceiling, matching MAX_TRACE_DEPTH in OptiXData.h.
   *  Increasing this requires recompiling the native library with a matching C++ change. */
  val MaxRayDepth: Int = 5

// Configuration for render quality options
case class RenderConfig(
  shadows: Boolean = true,
  transparentShadows: Boolean = false,  // Sprint 13.2: colored shadows through transparent objects
  antialiasing: Boolean = false,
  aaMaxDepth: Int = 2,
  aaThreshold: Float = 0.1f,
  maxRayDepth: Int = RenderLimits.MaxRayDepth,
  toneMappingOperator: Int = 0,    // 0=none/clip, 1=reinhard, 2=aces
  toneMappingExposure: Float = 1.0f
):
  require(aaMaxDepth >= 1 && aaMaxDepth <= 4, s"aaMaxDepth must be 1-4, got $aaMaxDepth")
  require(aaThreshold >= 0.0f && aaThreshold <= 1.0f, s"aaThreshold must be 0.0-1.0, got $aaThreshold")
  require(maxRayDepth >= 1 && maxRayDepth <= RenderLimits.MaxRayDepth,
    s"maxRayDepth must be 1-${RenderLimits.MaxRayDepth} (pipeline ceiling), got $maxRayDepth")

object RenderConfig:
  val Default: RenderConfig = RenderConfig()
  val HighQuality: RenderConfig = RenderConfig(
    shadows = true, antialiasing = true, aaMaxDepth = 3, aaThreshold = 0.05f)

// Configuration for Progressive Photon Mapping caustics
case class CausticsConfig(
  enabled: Boolean = false,
  photonsPerIteration: Int = 100000,
  iterations: Int = 10,
  initialRadius: Float = 1.0f,
  alpha: Float = 0.7f
):
  require(photonsPerIteration > 0 && photonsPerIteration <= CausticsConfig.MaxPhotonsPerIteration,
    s"photonsPerIteration must be 1-${CausticsConfig.MaxPhotonsPerIteration}, got $photonsPerIteration")
  require(iterations > 0 && iterations <= CausticsConfig.MaxIterations,
    s"iterations must be 1-${CausticsConfig.MaxIterations}, got $iterations")
  // initialRadius == AutoRadius (0.0) is a sentinel: the native PPM pass derives the gather
  // radius from scene geometry. Any positive value up to the max is an explicit override.
  require(initialRadius >= CausticsConfig.AutoRadius && initialRadius <= CausticsConfig.MaxInitialRadius,
    s"initialRadius must be ${CausticsConfig.AutoRadius} (auto) or up to ${CausticsConfig.MaxInitialRadius}, got $initialRadius")
  require(alpha > 0.0f && alpha < 1.0f, s"alpha must be 0.0-1.0 exclusive, got $alpha")

object CausticsConfig:
  val MaxPhotonsPerIteration: Int   = 10000000
  val MaxIterations: Int            = 1000
  val MaxInitialRadius: Float       = 10.0f
  /** Sentinel gather radius: native PPM derives it from scene geometry (optix-jni >= 0.1.13). */
  val AutoRadius: Float             = 0.0f

  val Disabled: CausticsConfig = CausticsConfig()
  val Default: CausticsConfig = CausticsConfig(enabled = true)
  val HighQuality: CausticsConfig = CausticsConfig(
    enabled = true, photonsPerIteration = 500000, iterations = 20, alpha = 0.8f)
