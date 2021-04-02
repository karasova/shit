package ru.mustakimov.vkbot

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("ru.mustakimov.vkbot")

        noClasses()
            .that()
            .resideInAnyPackage("ru.mustakimov.vkbot.service..")
            .or()
            .resideInAnyPackage("ru.mustakimov.vkbot.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..ru.mustakimov.vkbot.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
