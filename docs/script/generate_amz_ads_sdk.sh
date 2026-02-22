#!/bin/bash

# Exit on any error
set -e

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"
OPENAPI_GENERATOR_VERSION="7.3.0"
OPENAPI_GENERATOR_JAR="openapi-generator-cli-${OPENAPI_GENERATOR_VERSION}.jar"
OUT_DIR="${SCRIPT_DIR}/out/java"

# Target directory in the project
TARGET_DIR="${SCRIPT_DIR}/../../hzapp-module-erplus/hzapp-module-erplus-adv/src/main/java/com/hzltd/module/erplus/adv/adapter/amazon"

# 1. Download OpenAPI Generator if not present
cd "${SCRIPT_DIR}"
if [ ! -f "${OPENAPI_GENERATOR_JAR}" ]; then
    echo "Downloading OpenAPI Generator CLI ${OPENAPI_GENERATOR_VERSION}..."
    curl -sSL "https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${OPENAPI_GENERATOR_VERSION}/openapi-generator-cli-${OPENAPI_GENERATOR_VERSION}.jar" -o "${OPENAPI_GENERATOR_JAR}"
fi

# 2. Clean previous generation
echo "Cleaning previous output..."
rm -rf "${SCRIPT_DIR}/out"

# 3. Generate SDKs
# Common properties: use okhttp-gson or jackson, jakarta namespaces, and disable openApiNullable to reduce dependencies
COMMON_PROPS="serializationLibrary=jackson,javaxPackage=jakarta,openApiNullable=false,useJakartaEe=true,java.version=21"

echo "Generating ALL APIs (Profiles, reporting, etc.)..."
java -jar "${OPENAPI_GENERATOR_JAR}" generate \
    -i AmazonAdsAPIALL_prod_3p.json \
    -g java \
    --api-package com.hzltd.module.erplus.adv.adapter.amazon.api.all \
    --model-package com.hzltd.module.erplus.adv.adapter.amazon.model.all \
    --invoker-package com.hzltd.module.erplus.adv.adapter.amazon.client \
    -o out/java \
    --library native \
    --additional-properties="${COMMON_PROPS}"

echo "Generating SP (Sponsored Products) APIs..."
java -jar "${OPENAPI_GENERATOR_JAR}" generate \
    -i AmazonAdsAPISP_prod_3p.json \
    -g java \
    --api-package com.hzltd.module.erplus.adv.adapter.amazon.api.sp \
    --model-package com.hzltd.module.erplus.adv.adapter.amazon.model.sp \
    --invoker-package com.hzltd.module.erplus.adv.adapter.amazon.client \
    -o out/java \
    --library native \
    --additional-properties="${COMMON_PROPS}"

echo "Generating SB (Sponsored Brands) APIs..."
java -jar "${OPENAPI_GENERATOR_JAR}" generate \
    -i AmazonAdsAPISB_prod_3p.json \
    -g java \
    --api-package com.hzltd.module.erplus.adv.adapter.amazon.api.sb \
    --model-package com.hzltd.module.erplus.adv.adapter.amazon.model.sb \
    --invoker-package com.hzltd.module.erplus.adv.adapter.amazon.client \
    -o out/java \
    --library native \
    --additional-properties="${COMMON_PROPS}"

# 4. Copy generated files to the target module
echo "Copying generated files to module at: ${TARGET_DIR}"
rm -rf "${TARGET_DIR}/api" "${TARGET_DIR}/model" "${TARGET_DIR}/client"
mkdir -p "${TARGET_DIR}/api" "${TARGET_DIR}/model" "${TARGET_DIR}/client"
cp -R "${OUT_DIR}/src/main/java/com/hzltd/module/erplus/adv/adapter/amazon/"* "${TARGET_DIR}/"

echo "Generation and copy completed successfully."
