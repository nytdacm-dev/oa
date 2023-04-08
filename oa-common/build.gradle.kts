plugins {
    id("configure-kotlin")
    id("configure-ktlint")
    kotlin("plugin.allopen") version "1.8.10"
    kotlin("plugin.jpa") version "1.8.10"
    id("org.hibernate.orm") version "6.1.7.Final"
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.apache.commons:commons-lang3:3.12.0")
}

hibernate {
    enhancement {
//        enableLazyInitialization(true)
//        enableDirtyTracking(true)
        enableAssociationManagement(true)
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}
