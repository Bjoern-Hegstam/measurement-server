const path = require('path');

module.exports = {
    entry: './src/main/resources/public/scripts/app.js',
    output: {
        path: path.resolve(__dirname, 'target', 'classes', 'public'),
        filename: 'bundle.js'
    },
    module: {
        loaders: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                loader: 'babel-loader',
                query: {
                    presets: ['env']
                }
            }
        ]
    }
};