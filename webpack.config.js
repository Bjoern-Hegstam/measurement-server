const path = require('path');

module.exports = {
    entry: './src/main/resources/public/scripts/app.js',
    output: {
        path: path.resolve(__dirname, 'target', 'classes', 'public'),
        filename: 'bundle.js'
    }
};