#!/usr/bin/env bash
suffix=$(date +'%Y%m%d%H%S')
cp app/objectbox-models/default.json.bak "app/objectbox-models/default.json.bak.${suffix}"