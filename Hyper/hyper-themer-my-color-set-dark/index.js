module.exports.decorateConfig = config => {
  return Object.assign({}, config, {
    cursorColor: 'rgba(61, 255, 126, 0.5)',
    cursorAccentColor: '#18454E',
    foregroundColor: '#5F95A0',
    backgroundColor: '#18454E',
    selectionColor: 'rgba(0, 255, 179, 0.09999999999999998)',
    borderColor: '#00FFB3',
    colors: {
      black: '#18454E',
      red: '#FF5C5C',
      green: '#03E24E',
      yellow: '#94FFB8',
      blue: '#00FFB3',
      magenta: '#66FF99',
      cyan: '#00FFB3',
      white: '#5F95A0',
      lightBlack: '#24525C',
      lightRed: '#03E24E',
      lightGreen: '#03E24E',
      lightYellow: '#94FFB8',
      lightBlue: '#00FFB3',
      lightMagenta: '#66FF99',
      lightCyan: '#00FFB3',
      lightWhite: '#6BA2AE',
    },
  });
};