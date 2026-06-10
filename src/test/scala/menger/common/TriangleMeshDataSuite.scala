package menger.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TriangleMeshDataSuite extends AnyFlatSpec with Matchers:

  private val Vertex6 = Array(1.0f, 2.0f, 3.0f, 0.0f, 1.0f, 0.0f)
  private val Vertex8 = Array(1.0f, 2.0f, 3.0f, 0.0f, 1.0f, 0.0f, 0.25f, 0.75f)
  private val Vertex8B = Array(4.0f, 5.0f, 6.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f)
  private val Vertex9 = Array(1.0f, 2.0f, 3.0f, 0.0f, 1.0f, 0.0f, 0.25f, 0.75f, 0.5f)

  "TriangleMeshData" should "accept supported vertex strides and report counts" in:
    val legacy = TriangleMeshData(Vertex6, Array(0, 0, 0), TriangleMeshData.LegacyVertexStride)
    val uv = TriangleMeshData(Vertex8, Array(0, 0, 0))
    val alpha = TriangleMeshData(Vertex9, Array(0, 0, 0), TriangleMeshData.VertexStrideWithAlpha)

    legacy.numVertices shouldBe 1
    legacy.numTriangles shouldBe 1
    legacy.hasUVs shouldBe false
    uv.numVertices shouldBe 1
    uv.numTriangles shouldBe 1
    uv.hasUVs shouldBe true
    alpha.numVertices shouldBe 1
    alpha.hasUVs shouldBe false

  it should "reject unsupported strides and malformed arrays" in:
    an[IllegalArgumentException] shouldBe thrownBy(TriangleMeshData(Vertex8, Array(0, 0, 0), 7))
    an[IllegalArgumentException] shouldBe thrownBy(
      TriangleMeshData(Vertex8.dropRight(1), Array(0, 0, 0))
    )
    an[IllegalArgumentException] shouldBe thrownBy(TriangleMeshData(Vertex8, Array(0, 1)))

  it should "provide an empty legacy mesh" in:
    TriangleMeshData.empty.vertices shouldBe empty
    TriangleMeshData.empty.indices shouldBe empty
    TriangleMeshData.empty.vertexStride shouldBe TriangleMeshData.LegacyVertexStride
    TriangleMeshData.empty.numVertices shouldBe 0
    TriangleMeshData.empty.numTriangles shouldBe 0

  it should "merge no meshes or a single mesh without rewriting data" in:
    val mesh = TriangleMeshData(Vertex8, Array(0, 0, 0))

    TriangleMeshData.merge(Seq.empty) shouldBe TriangleMeshData.empty
    TriangleMeshData.merge(Seq(mesh)) shouldBe mesh

  it should "merge multiple meshes and adjust triangle indices by vertex offset" in:
    val first = TriangleMeshData(Vertex8, Array(0, 0, 0))
    val second = TriangleMeshData(Vertex8B, Array(0, 0, 0))
    val merged = TriangleMeshData.merge(Seq(first, second))

    merged.vertices.toSeq shouldBe (Vertex8 ++ Vertex8B).toSeq
    merged.indices.toSeq shouldBe Seq(0, 0, 0, 1, 1, 1)
    merged.vertexStride shouldBe TriangleMeshData.DefaultVertexStride
    merged.numVertices shouldBe 2

  it should "reject merges with mixed vertex strides" in:
    val legacy = TriangleMeshData(Vertex6, Array(0, 0, 0), TriangleMeshData.LegacyVertexStride)
    val uv = TriangleMeshData(Vertex8, Array(0, 0, 0))

    an[IllegalArgumentException] shouldBe thrownBy(TriangleMeshData.merge(Seq(legacy, uv)))

  it should "expand vertex positions along normals without mutating the source mesh" in:
    val mesh = TriangleMeshData(Vertex8, Array(0, 0, 0))
    val expanded = TriangleMeshData.expandAlongNormals(mesh, 0.5f)

    expanded.vertices.take(6).toSeq shouldBe Seq(1.0f, 2.5f, 3.0f, 0.0f, 1.0f, 0.0f)
    expanded.vertices.drop(6).toSeq shouldBe Seq(0.25f, 0.75f)
    expanded.indices shouldBe mesh.indices
    expanded.vertexStride shouldBe mesh.vertexStride
    mesh.vertices.toSeq shouldBe Vertex8.toSeq

  it should "add alpha to stride eight meshes" in:
    val mesh = TriangleMeshData(Vertex8 ++ Vertex8B, Array(0, 1, 0))
    val withAlpha = TriangleMeshData.withAlpha(mesh, 0.4f)

    withAlpha.vertexStride shouldBe TriangleMeshData.VertexStrideWithAlpha
    withAlpha.numVertices shouldBe 2
    withAlpha.indices shouldBe mesh.indices
    withAlpha.vertices.slice(0, 8).toSeq shouldBe Vertex8.toSeq
    withAlpha.vertices(8) shouldBe 0.4f
    withAlpha.vertices.slice(9, 17).toSeq shouldBe Vertex8B.toSeq
    withAlpha.vertices(17) shouldBe 0.4f

  it should "reject alpha conversion for unsupported source stride or alpha values" in:
    val legacy = TriangleMeshData(Vertex6, Array(0, 0, 0), TriangleMeshData.LegacyVertexStride)
    val uv = TriangleMeshData(Vertex8, Array(0, 0, 0))

    an[IllegalArgumentException] shouldBe thrownBy(TriangleMeshData.withAlpha(legacy, 0.5f))
    an[IllegalArgumentException] shouldBe thrownBy(TriangleMeshData.withAlpha(uv, -0.01f))
    an[IllegalArgumentException] shouldBe thrownBy(TriangleMeshData.withAlpha(uv, 1.01f))
