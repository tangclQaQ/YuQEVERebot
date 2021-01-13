buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

plugins {
    java
    id("com.github.johnrengelman.shadow") version "5.2.0"
    eclipse
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    // Gradle 的 扫包是从上至下的，按照当前顺序，则为： 冰糕Luminous的仓库 -> 阿里云的仓库 -> 中央库 -> jcenter
    // YuQ-Mirai 的依赖位于中央库和 jcenter。
    // Dev 包均位于 IceCream 的 Maven 仓库。

    // 这是由 冰糕Luminous 提供的 Maven 仓库镜像。
    maven("https://oss.heavenark.com/repository/MavenPublic/")
    // 这是由 阿里云 提供的 Maven 仓库镜像。
    maven("https://maven.aliyun.com/repository/public")
    // 需要同时启用 中央库 及 jcenter。
    mavenCentral()
    jcenter()
    maven("https://maven.IceCreamQAQ.com/repository/maven-public/")
}

dependencies {
    implementation("com.IceCreamQAQ.YuQ:YuQ-ArtQQ:0.0.6.10-R37")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    jar {
        finalizedBy(shadowJar)
    }

    shadowJar {
        manifest {
            attributes["Main-Class"] = "wiki.IceCream.yuq.demo.Start"
        }

        from("./") {
            include("build.gradle")
        }
    }
}