package menger.common

import com.tngtech.archunit.core.importer.{ClassFileImporter, ImportOption, Location}
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.scalatest.flatspec.AnyFlatSpec

class ArchUnitSpec extends AnyFlatSpec {

  // sbt puts test classes under target/scala-X.Y.Z/test-classes/, which ArchUnit's own
  // DoNotIncludeTests pattern (".*/target/test-classes/.*") misses.
  private val doNotIncludeSbtTestClasses: ImportOption =
    (location: Location) => !location.matches(java.util.regex.Pattern.compile(".*test-classes.*"))

  private val productionClasses = new ClassFileImporter()
    .withImportOption(new DoNotIncludeTests())
    .withImportOption(doNotIncludeSbtTestClasses)
    .importPackages("menger.common")

  // menger-common is the dependency-free base of the workspace (arc42 §5.2.1: "Java/Scala
  // stdlib only"). The rule here used to be a cycle check over sub-packages of a flat package,
  // so it matched zero slices and, with allowEmptyShould(true), could not fail (review
  // 2026-10-14, menger#90). This one states the invariant itself and fails on an empty import.
  "menger.common" should "depend only on the JDK, the Scala library and itself" in {
    classes()
      .should()
      .onlyDependOnClassesThat()
      .resideInAnyPackage("java..", "scala..", "menger.common..")
      .check(productionClasses)
  }
}
