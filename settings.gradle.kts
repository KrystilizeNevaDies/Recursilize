rootProject.name = "Recursilize"
include("core")
include("minestom")
include("platform-api")
include("tests")
include("platform-api:src:main:test")
findProject(":platform-api:src:main:test")?.name = "test"
include("platform-api:test")
findProject(":platform-api:test")?.name = "test"
