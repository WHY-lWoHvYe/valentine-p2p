dependencies {
    implementation(project(":unicorn-core")) {
        // 括号里是Groovy的语法，定义map
        exclude(group = "org.springdoc", module = "springdoc-openapi-starter-webmvc-ui")
        // core本是基于servlet的，而resourceServer基于reactive，所以需要exclude此处
        exclude(group = "org.springframework", module = "spring-webmvc")
    }
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    api(libs.springdoc.webflux.ui)
}
