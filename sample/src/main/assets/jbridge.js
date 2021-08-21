/**
 * minify with https://javascript-minifier.com/
 */
(function () {
  if (window.jBridge) return;

  (function () {
    var callbackMap = {};
    var callMap = {};

    function native(cmd, method, arg) {
      prompt('jbridge://app/' + cmd + '/' + method + '/' + arg);
    }

    function register(method, handler) {
      callMap[method] = handler;
    }

    function invoke(method, arg, callback) {
      if (callback) callbackMap[method] = callback;
      native('call', method, arg);
    }

    function __call(method, arg) {
      var call = callMap[method];
      call && call(arg, function (data) {
        native('callback', method, arg);
      });
    }

    function __callback(method, data) {
      var callback = callbackMap[method];
      callback && callback(data);
    }

    window.jBridge = {
      register: register,
      invoke: invoke,
      __call: __call,
      __callback: __callback
    };

    console.log("hello, jBridge");
  })();
})();
