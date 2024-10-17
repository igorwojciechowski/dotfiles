module.exports.decorateConfig = config => {
  return Object.assign({}, config, {
    cursorColor: 'rgba(31, 145, 163, 0.5)',
    cursorAccentColor: '#07283D',
    foregroundColor: '#749DBC',
    backgroundColor: '#07283D',
    selectionColor: 'rgba(85, 171, 195, 0.09999999999999998)',
    borderColor: '#54C0B6',
    colors: {
      black: '#07283D',
      red: '#D91717',
      green: '#53D0A6',
      yellow: '#6BCCDB',
      blue: '#55ABC3',
      magenta: '#1F91A3',
      cyan: '#54C0B6',
      white: '#749DBC',
      lightBlack: '#193C52',
      lightRed: '#4C9DA9',
      lightGreen: '#53D0A6',
      lightYellow: '#6BCCDB',
      lightBlue: '#55ABC3',
      lightMagenta: '#1F91A3',
      lightCyan: '#54C0B6',
      lightWhite: '#86B1D1',
    },
  });
};