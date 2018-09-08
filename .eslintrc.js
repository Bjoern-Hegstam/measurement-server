module.exports = {
    "extends": "airbnb",
    "parser": "babel-eslint",
    "env": {
        "jest": true,
    },
    "rules": {
        "max-len": [1, 120],
        "indent": ["error", 4],
        "react/jsx-indent": [2, 4],
        "react/jsx-indent-props": [2, 4],
        "linebreak-style": "off",
        "react/destructuring-assignment": [0],
        "import/no-named-as-default": [0],
    }
};