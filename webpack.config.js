const path = require('path');

module.exports = {
    entry: './src/main/resources/public/js/index.jsx',
    output: {
        path: path.resolve(__dirname, 'target', 'classes', 'public', 'js'),
        filename: 'bundle.js'
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['env', 'react'],
                        plugins: [
                            'transform-class-properties',
                            'transform-object-rest-spread'
                        ]
                    }
                }
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx'],
    },
    devtool: 'source-map'
};