function (out) {
	out.push('<i ', this.domAttrs_(), '>');

	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</i>');
}