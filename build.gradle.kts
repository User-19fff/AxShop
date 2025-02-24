plugins {
    id("java")
    id("com.gradleup.shadow") version("8.3.2")
    id("io.github.revxrsal.zapper") version("1.0.2")
    id("io.freefair.lombok") version("8.11")
}

group = "net.coma112"
version = "1.0.0"

repositories {
    mavenCentral()

    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.artillex-studios.com/releases")
    //maven("https://repo.nightexpressdev.com/releases") // coinsengine
    //maven("https://repo.magmaguy.com/releases") // elitemobs
    //maven("https://repo.techscode.com/repository/techscode-apis/") // ultraeconomy
}

dependencies {
    // beasttoken - jar fajl beolvasas
    // playerpoints - jar fajl beolvasas

    implementation("com.artillexstudios.axapi:axapi:1.4.513:all")

    zap("com.github.Anon8281:UniversalScheduler:0.1.6")
    //zap("su.nightexpress.coinsengine:CoinsEngine:2.4.1") // coinsengine

    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.36")
    //compileOnly("com.magmaguy:EliteMobs:9.1.9") // elitemobs
    //compileOnly("me.TechsCode:UltraEconomyAPI:2.10.3") // ultra economy

    //compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") {
    //    exclude(module = "org.bukkit.bukkit") // vault
    //}
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

zapper {
    libsFolder = "libs"
    relocationPrefix = "net.coma112.axshop.libs"

    repositories { includeProjectRepositories() }

    relocate("com.github.Anon8281.universalScheduler", "universalScheduler")
    relocate("com.artillexstudios.axapi", "axapi")
}