#!/bin/sh
set -e

# Update config.js with current environment variables
# This allows passing VITE_BASE_URL via 'docker run -e VITE_BASE_URL=...'
echo "Updating /usr/share/nginx/html/config.js with VITE_BASE_URL=${VITE_BASE_URL:-(empty)}"

cat <<EOF > /usr/share/nginx/html/config.js
window._CONFIG = {
  VITE_BASE_URL: '${VITE_BASE_URL:-}'
};
EOF

# Execute the main command
exec "$@"
