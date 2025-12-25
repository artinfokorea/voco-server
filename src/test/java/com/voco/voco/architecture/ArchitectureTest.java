package com.voco.voco.architecture;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

class ArchitectureTest {

	private static JavaClasses classes;

	@BeforeAll
	static void setUp() {
		classes = new ClassFileImporter()
			.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
			.importPackages("com.voco.voco");
	}

	@Nested
	@DisplayName("레이어 의존성 규칙")
	class LayerDependencyRules {

		@Test
		@DisplayName("도메인 레이어는 다른 레이어에 의존하지 않는다")
		void domain_should_not_depend_on_other_layers() {
			noClasses()
				.that().resideInAPackage("..domain..")
				.should().dependOnClassesThat().resideInAnyPackage(
					"..application..",
					"..presentation..",
					"..infrastructure.."
				)
				.check(classes);
		}

		@Test
		@DisplayName("application 레이어는 presentation, infrastructure에 의존하지 않는다")
		void application_should_not_depend_on_presentation_and_infrastructure() {
			noClasses()
				.that().resideInAPackage("..application..")
				.should().dependOnClassesThat().resideInAnyPackage(
					"..presentation..",
					"..infrastructure.."
				)
				.check(classes);
		}

		@Test
		@DisplayName("presentation 레이어는 infrastructure에 의존하지 않는다")
		void presentation_should_not_depend_on_infrastructure() {
			noClasses()
				.that().resideInAPackage("..presentation..")
				.should().dependOnClassesThat().resideInAPackage("..infrastructure..")
				.check(classes);
		}
	}

	@Nested
	@DisplayName("레이어 아키텍처 검증")
	class LayeredArchitectureRules {

		@Test
		@DisplayName("레이어드 아키텍처를 준수한다")
		void should_follow_layered_architecture() {
			layeredArchitecture()
				.consideringAllDependencies()
				.layer("Presentation").definedBy("..presentation..")
				.layer("Application").definedBy("..application..")
				.layer("Domain").definedBy("..domain..")
				.layer("Infrastructure").definedBy("..infrastructure..")
				.layer("Batch").definedBy("..batch..")

				.whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
				.whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure", "Batch")
				.whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Batch")
				.whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
				.whereLayer("Batch").mayNotBeAccessedByAnyLayer()

				.ignoreDependency(
					resideInAPackage("..presentation.."),
					resideInAPackage("..domain.model..").and(DescribedPredicate.describe("is enum", JavaClass::isEnum))
				)

				.check(classes);
		}
	}
}