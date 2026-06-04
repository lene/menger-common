package menger.common

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.scalatest.flatspec.AnyFlatSpec

class ArchUnitSpec extends AnyFlatSpec {

  private val classes = new ClassFileImporter()
    .importPackages("menger.common")

  "Package structure" should "be free of circular dependencies" in {
    slices()
      .matching("menger.common.(*)..")
      .should()
      .beFreeOfCycles()
      .allowEmptyShould(true)
      .check(classes)
  }
}
