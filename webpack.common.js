const path = require('path');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

// TODO:
// * Copy dist folder to target
// * verify that style (less2css) is included
// * use ExtractTextPlugin to create separate css file for production (see https://github.com/webpack-contrib/less-loader)
// * Add way to toggle between dev and production when building with maven (profile that activates based on env variable (ex. DEBUG=true)?)
// * Split style into one file per component?

module.exports = {
    entry: './src/main/resources/public/js/index.jsx',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].bundle.js'
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
            },
            {
                test: /\.less$/,
                use: [{
                    loader: "style-loader" // creates style nodes from JS strings
                }, {
                    loader: "css-loader" // translates CSS into CommonJS
                }, {
                    loader: "less-loader" // compiles Less to CSS
                }]
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx', '.less'],
    },
    plugins: [
        new CleanWebpackPlugin(['dist']),
        new HtmlWebpackPlugin()
    ]
};