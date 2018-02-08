const path = require('path');

module.exports = {
    entry: './src/main/resources/public/scripts/app.js',
    output: {
        path: path.resolve(__dirname, 'target', 'classes', 'public', 'scripts'),
        filename: 'bundle.js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['env']
                    }
                }
            }
        ]
    },
    devtool: 'source-map'
};