source .env

[ -e dist ] && rm -r dist

mkdir dist
cp -r lib dist/lib
jar cmf src/main/resources/META-INF/MANIFEST.MF "dist/$PACKAGE.jar" -C out/classes . -C src/main/resources/ .