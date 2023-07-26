#!/bin/bash

[ -z "${APK_KS_PASS}" ] && { echo "Missing keystore password (KS_PASS)"; exit 1; }
[ -z "${APK_KS}" ] && { echo "Missing keystore file (KS)"; exit 1; }
[ -z "${APK_KS_ALIAS}" ] && { echo "Missing keystore alias (KS_ALIAS)"; exit 1; }

echo -n "$APK_KS" | base64 -d > /tmp/keystore.keystore

apksigner sign --ks /tmp/keystore.keystore --ks-pass pass:${APK_KS_PASS} --ks-key-alias ${APK_KS_ALIAS} --out $1 $2