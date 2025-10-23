/* Lightweight client-side debug instrumentation for forms and network */
(function () {
  try {
    if (window.__DEBUG__ === false) return; // allow opt-out
    window.__DEBUG__ = true;

    var origConsole = window.console || {};
    function ts() { try { return new Date().toISOString(); } catch (_) { return Date.now(); } }
    ["log", "info", "warn", "error", "debug"].forEach(function (level) {
      try {
        var orig = typeof origConsole[level] === "function" ? origConsole[level].bind(origConsole) : function () {};
        console[level] = function () {
          var args = Array.prototype.slice.call(arguments);
          try { orig.apply(origConsole, ["[DEBUG][" + ts() + "]"].concat(args)); } catch (_) {}
        };
      } catch (_) {}
    });

    function mask(name, type, value) {
      var n = (name || "").toLowerCase();
      var t = (type || "").toLowerCase();
      if (t === "password" || n.includes("password") || n.includes("clave")) {
        var len = (value || "").length;
        var stars = "";
        try { stars = "*".repeat(Math.min(8, len)); } catch (_) { stars = "********"; }
        return stars + " (len=" + len + ")";
      }
      if (t === "file") return "[file input]";
      return value;
    }

    function logPage() {
      try { console.info("[Page] title:", document.title); } catch (_) {}
      try { console.info("[Page] url:", location.href); } catch (_) {}
      try { console.info("[Page] referrer:", document.referrer); } catch (_) {}
      try { console.info("[Navigator] userAgent:", navigator.userAgent); } catch (_) {}
      try {
        var errNodes = Array.from(document.querySelectorAll('[style*="color:red"], .error, [data-error]'))
          .map(function (n) { return (n.textContent || "").trim(); }).filter(Boolean);
        if (errNodes.length) console.warn("[Page Errors]", errNodes);
      } catch (_) {}
    }

    function logForm(form) {
      try {
        var attrs = { id: form.id, name: form.name, method: form.method, action: form.action, enctype: form.enctype || form.encoding, noValidate: form.noValidate };
        console.info("[Form]", attrs);
        var fields = form.querySelectorAll("input,select,textarea");
        Array.prototype.forEach.call(fields, function (el) {
          try {
            var desc = { tag: el.tagName, type: el.type, name: el.name, id: el.id, required: el.required, disabled: el.disabled, readOnly: el.readOnly };
            if (el.tagName === "SELECT") {
              desc.options = Array.from(el.options || []).map(function (o) { return { value: o.value, text: o.text, selected: o.selected }; });
            } else if (el.type === "file") {
              desc.files = el.files ? Array.from(el.files).map(function (f) { return { name: f.name, size: f.size, type: f.type }; }) : [];
            } else {
              desc.value = mask(el.name, el.type, el.value);
            }
            console.debug("[Field]", desc);
          } catch (_) {}
        });

        form.addEventListener("submit", function () {
          try {
            console.info("[Form Submit] id/name", form.id, form.name);
            var fd = new FormData(form);
            var out = [];
            fd.forEach(function (v, k) {
              try {
                if (v instanceof File) out.push({ name: k, file: { name: v.name, size: v.size, type: v.type } });
                else out.push({ name: k, value: mask(k, (form.querySelector('[name="' + k + '"]') || {}).type, v) });
              } catch (_) {}
            });
            console.info("[FormData]", out);
          } catch (e) { console.warn("[Form Submit] error collecting data", e && e.message); }
        }, true);

        Array.prototype.forEach.call(fields, function (el) {
          try {
            el.addEventListener("change", function () {
              try {
                if (el.type === "file") {
                  var files = el.files ? Array.from(el.files).map(function (f) { return { name: f.name, size: f.size, type: f.type }; }) : [];
                  console.debug("[Change]", el.name || el.id, "files:", files);
                } else {
                  console.debug("[Change]", el.name || el.id, "value:", mask(el.name, el.type, el.value));
                }
              } catch (_) {}
            });
          } catch (_) {}
        });
      } catch (_) {}
    }

    function patchNetwork() {
      try {
        if (window.fetch) {
          var origFetch = window.fetch.bind(window);
          window.fetch = function (input, init) {
            try { console.info("[fetch]", (init && init.method) || "GET", (input && input.url) || input); } catch (_) {}
            return origFetch(input, init).then(function (res) {
              try { console.info("[fetch:response]", res.status, res.url); } catch (_) {}
              return res;
            }).catch(function (err) {
              try { console.error("[fetch:error]", err && err.message); } catch (_) {}
              throw err;
            });
          };
        }
      } catch (_) {}
      try {
        var X = window.XMLHttpRequest && window.XMLHttpRequest.prototype;
        if (X && X.open && X.send) {
          var origOpen = X.open, origSend = X.send;
          X.open = function (m, u) { this.__dbg = { m: m, u: u }; return origOpen.apply(this, arguments); };
          X.send = function (body) {
            try {
              var self = this;
              console.info("[xhr]", self.__dbg && self.__dbg.m, self.__dbg && self.__dbg.u);
              self.addEventListener('load', function () { console.info("[xhr:load]", self.status, self.responseURL || (self.__dbg && self.__dbg.u)); });
              self.addEventListener('error', function () { console.error("[xhr:error]", self.__dbg && self.__dbg.m, self.__dbg && self.__dbg.u); });
            } catch (_) {}
            return origSend.apply(this, arguments);
          };
        }
      } catch (_) {}
    }

    window.addEventListener('error', function (e) { try { console.error("[window.onerror]", e.message, e.filename + ":" + e.lineno + ":" + e.colno); } catch (_) {} });
    window.addEventListener('unhandledrejection', function (e) { try { console.error("[promise.unhandled]", (e.reason && (e.reason.message || e.reason)) || e); } catch (_) {} });

    document.addEventListener('DOMContentLoaded', function () {
      logPage();
      patchNetwork();
      try { Array.prototype.forEach.call(document.forms || [], logForm); } catch (_) {}
      try {
        var sel = document.querySelector('#select-zona-registro');
        if (sel) {
          var opts = Array.from(sel.options || []);
          console.info('[Zonas] count=', opts.length);
          opts.forEach(function (o, i) { console.debug('[Zonas] opt', i, { value: o.value, text: o.text, selected: o.selected }); });
        }
      } catch (_) {}
    });
  } catch (e) {
    try { (window.console || { error: function () {} }).error('[DEBUG] Instrumentation failed:', e && e.message); } catch (_) {}
  }
})();

