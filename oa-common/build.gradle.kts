plugins {
    id("org.hibernate.orm") version "6.1.6.Final"
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("commons-lang:commons-lang:2.6")
}

hibernate {
    enhancement {
        enableLazyInitialization(true)
        enableDirtyTracking(true)
        enableAssociationManagement(true)
    }
}
