BOBLIGHT_DIR=/storage/programs/clients
JAVA=/storage/programs/jre/bin/java

BOBLIGHT_OPTIONS=-u 16 -x -o speed=50 -o interpolation=true -o threshold=20 -o saturation=2 -o value=10

$JAVA -jar $BOBLIGHT_DIR/${project.build.finalName}.jar $BOBLIGHT_OPTIONS &
