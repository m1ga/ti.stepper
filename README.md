```
var stepper = require("ti.stepper");

stepper.addEventListener("steps", function(e) {
	console.log(e);
	if (e.type == "stepCounter") {
		lbl.text = e.type + ": " + e.count;
	} else {
		lbl2.text = e.type + ": " + e.count;
	}
});

var win = Ti.UI.createWindow({
	backgroundColor: "#fff",
	layout: "vertical"
});
var lbl = Ti.UI.createLabel({
	text: "-",
	color: "#000"
});
var lbl2 = Ti.UI.createLabel({
	text: "-",
	color: "#000"
});
win.add([lbl, lbl2]);
win.open();
win.addEventListener("open", function() {
	stepper.create();
})
win.addEventListener("resume", function() {
	stepper.registerSensor();
})
win.addEventListener("pause", function() {
	stepper.unregisterSensor();
})

```
