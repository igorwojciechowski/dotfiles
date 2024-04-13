module.exports.decorateConfig = config => {
  return Object.assign({}, config, {
    cursorColor: 'rgba(58, 170, 187, 0.5)',
    cursorAccentColor: '#07283D',
    foregroundColor: '#749DBC',
    backgroundColor: '#07283D',
    selectionColor: 'rgba(93, 187, 213, 0.09999999999999998)',
    borderColor: '#54C0B6',
    colors: {
      black: '#07283D',
      red: '#D95757',
      green: '#98E1C5',
      yellow: '#9EDEE5',
      blue: '#5DBBD5',
      magenta: '#3AAABB',
      cyan: '#54C0B6',
      white: '#749DBC',
      lightBlack: '#193C52',
      lightRed: '#3CC8BC',
      lightGreen: '#98E1C5',
      lightYellow: '#9EDEE5',
      lightBlue: '#5DBBD5',
      lightMagenta: '#3AAABB',
      lightCyan: '#54C0B6',
      lightWhite: '#86B1D1',
    },
  });
};