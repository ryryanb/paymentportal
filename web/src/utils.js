const Utils = {
  logout: function () {
    localStorage.clear();
    window.location = "/psbbiller";
  }
}

export default Utils;