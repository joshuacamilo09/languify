#!/bin/bash

echo "Generating JWT secret key..."
SECRET=$(openssl rand -base64 32)
echo ""
echo "Your JWT secret key:"
echo "$SECRET"
