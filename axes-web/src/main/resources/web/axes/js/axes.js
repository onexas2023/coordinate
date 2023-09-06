(function() {
	if (window._axes)
		return;
	// jquery special event to dom destroy
	// http://stackoverflow.com/questions/2200494/jquery-trigger-event-when-an-element-is-removed-from-the-dom
	$.event.special.destroyed = {
		remove: function(o) {
			if (o.handler) {
				o.handler();
			}
		}
	}

	var axes = {};

	axes.fullscreen = function(element) {
		// Supports most browsers and their versions.
		var requestMethod = element.requestFullScreen || element.webkitRequestFullScreen || element.mozRequestFullScreen || element.msRequestFullScreen;
		if (requestMethod) { // Native full screen.
			requestMethod.call(element);
		} else if (typeof window.ActiveXObject !== "undefined") { // Older IE.
			var wscript = new ActiveXObject("WScript.Shell");
			if (wscript !== null) {
				wscript.SendKeys("{F11}");
			}
		}
	};

	axes.pushUrl = function(url, title) {
		window.history.pushState({ _axes: true }, '', url);
		document.title = title;
	}
	axes.replaceUrl = function(url, title) {
		window.history.replaceState({ _axes: true }, '', url);
		document.title = title;
	}

	window.onpopstate = function(evt) {
		if (!evt.state || evt.state._axes) {
			window.location.reload();
		}
	};

	axes.highlight = function(element, word) {
		if (element.$$hl) {
			element.$$hl.removeHighlight();
		}
		if (!word) {
			return;
		}
		var h = element.$$hl = jq(element);
		h.highlight(word);
	};


	axes.toggleVisibleIfAny = function(widget, animation) {
		if (!widget)
			return;

		widget.setVisible(!widget.isVisible());
	}
	axes.fireOnSizeIfAny = function(widget) {
		if (!widget) {
			return;
		}

		zWatch.fireDown('onSize', widget);
	}

	function calculateMy(position) {
		var arr = position.split(" ");
		var my = [];

		for (var i = 0; i < arr.length; i++) {
			switch (arr[i]) {
				case 'top':
					my.push('bottom');
					break;
				case 'bottom':
					my.push('top');
					break;
				case 'left':
					my.push('right');
					break;
				case 'right':
					my.push('left');
					break;
				default:
					my.push('center');
			}
		}
		return my.join(' ');
	}

	var lastMsgFaker;
	axes.messageFadeout = 500;
	axes.message = function(selector, type, msg, atPos, myPos, duration, closable) {
		var target, my, faker;
		if (!selector) {
			target = faker = $("<div class='axes-msg-faker'><div>").appendTo(document.body);
			if (lastMsgFaker) {
				var lmf = $(lastMsgFaker);
				var api = lmf.qtip('api');
				var id = api.get('id');
				var tipelm = $("#qtip-" + id);
				var offset = lmf.offset();
				var bodyHeight = $(document.body).outerHeight();
				var bottom = bodyHeight - offset.top + tipelm.outerHeight() + 1;
				if (bottom < ($(document.body).outerHeight() / 2)) {
					faker.css({ bottom: bottom, left: offset.left });
				}
			}
			lastMsgFaker = faker[0];
		} else {
			target = $(selector);
		}

		if (target.length == 0) {
			return;
		}

		// clear previous
		axes.clearMessage(selector);

		target[0]._zsmsg = true;// zs wrong value
		target[0]._zsmsgDtyd = function() {
			target.qtip('destroy', true);
		};

		if (!type) {
			type = "notify";
		}
		if (!msg) {
			msg = "No message";
		}
		if (!atPos) {
			atPos = "center center"
		}
		if (!myPos) {
			myPos = calculateMy(atPos);
		}

		var cnt = $("<div class='axes-msg-content'></div>");
		cnt.appendTo(document.body);
		$("<div class='axes-msg-text'>").text(msg).appendTo(cnt);

		var opt = {
			content: {
				text: cnt
			},
			position: {
				my: myPos,
				at: atPos,
				target: target,
				viewport: $(document.body),
				adjust: {
					method: 'flip'
				}
			},
			hide: {
				// hide manually
				event: false,
				effect: function(offset) {
					$(this).fadeOut(axes.messageFadeout);
				}
			},
			style: {
				classes: 'qtip-shadow axes-msg ' + 'axes-' + type
			},
			events: {
				hide: function(event, api) {
					if (faker) {
						if (lastMsgFaker == faker[0]) {
							lastMsgFaker = null;
						}
						faker.remove();
					} else {
						axes.clearMessage(selector);
					}
				}
			}
		};
		var newtip = target.qtip(opt);
		var tipid = newtip.qtip('api').get('id');
		target.qtip('show', true);
		target.bind('destroyed', target[0]._zsmsgDtyd);

		if (closable) {
			var closeBtn = $("<div class='axes-msg-btn'><i class='fa fa-times'></i></div>").appendTo(cnt);
			closeBtn.click(function() {
				newtip.qtip("hide");
			});
			if (duration <= 0) {
				closeBtn.addClass("axes-msg-btn-show");
			}
		}
		if (duration > 0) {
			setTimeout(function() {
				var api = newtip.qtip('api');
				if (api) {
					var tipid2 = api.get('id');
					if (tipid == tipid2) {
						newtip.qtip("hide");
					}
				}
			}, duration);
		}
	}

	axes.clearMessage = function(selector) {
		var target;
		if (!selector) {
			target = $(document.body);
		} else {
			target = $(selector);
		}
		if (target.length == 0) {
			return;
		}
		if (target[0]._zsmsg) {
			target.qtip('destroy', true);
			delete target[0]._zsmsg;
		}
		if (target[0]._zsmsgDtyd) {
			target.unbind("destroyed", target[0]._zsmsgDtyd);
			delete target[0]._zsmsgDtyd;
		}
	}

	axes.wrongValue = function(selector, msg, container, viewport) {
		var target = $(selector);

		if (target.length == 0) {
			return;
		}

		// clear previous
		axes.clearWrongValue(selector);

		target[0]._zswv = true;// zs wrong value
		target[0]._zswvDtyd = function() {
			$(selector).qtip('destroy', true);
		};

		var opt = {
			content: {
				text: msg
			},
			position: {
				my: 'top left',
				at: 'bottom center',
				target: target,
			},
			style: {
				classes: 'qtip-wrong-value qtip-rounded qtip-shadow'
			}
		};
		if (container) {
			// if inside zk component cave, use cave (for scrolling)
			var cnt = $(container);
			var cave = $(container + '-cave');
			if (cave.length > 0) {
				opt.position.container = cave;
				opt.position.viewport = cnt;
			} else {
				opt.position.container = cnt;
				opt.position.viewport = cnt.parent();
			}
		}
		if (viewport) {
			opt.position.viewport = $(viewport);
		}

		target.qtip(opt);
		target.qtip('show', true);
		target.bind('destroyed', target[0]._zswvDtyd);
		target.addClass('wrong-value');
	}
	axes.clearWrongValue = function(selector) {
		var target = $(selector);
		if (target.length == 0) {
			return;
		}
		if (target[0]._zswv) {
			target.qtip('destroy', true);
			delete target[0]._zswv;
		}
		if (target[0]._zswvDtyd) {
			target.unbind("destroyed", target[0]._zswvDtyd);
			delete target[0]._zswvDtyd;
		}
		target.removeClass('wrong-value');


	}

	axes.centerIfAny = function(widget, refwidget) {
		if (!widget || !refwidget) {
			return;
		}

		var w = jq(widget.$n());
		var ref = jq(refwidget.$n());
		var top = ref.height() / 2 - w.height() / 2;
		var left = ref.width() / 2 - w.width() / 2;
		top = top < 0 ? 0 : top;
		left = left < 0 ? 0 : left;
		w.css({ top: top, left: left });
	}
	axes.leftTopIfAny = function(widget, left, top) {
		if (!widget) {
			return;
		}
		var w = jq(widget.$n());
		w.css({ top: top, left: left });
	}
	axes.copyToClipboard = function(text) {
    	const listener = (e) => {
	        e.clipboardData.setData('text/plain', text);
	        e.preventDefault();
        	document.removeEventListener('copy', listener);
    	};
    	window.document.addEventListener('copy', listener);
    	window.document.execCommand('copy');
    }

	window._axes = axes;

	var fn = document.ondblclick;
	// prevent double click selection
	document.ondblclick = function(evt) {

		var elm = evt.target;
		var name = elm.nodeName.toLowerCase();
		if (name == "textarea" || name == "input") {
			return;
		}

		// #23: timebox has wrong time shift when click up/down button
		var w = zk.isLoaded("zul.db") && zk.Widget.$(elm);
		if (w && w.$instanceof(zul.db.Timebox)) {
			return;
		}

		if (window.getSelection)
			window.getSelection().removeAllRanges();
		else if (document.selection)
			document.selection.empty();

		if (fn) {
			fn.apply(this, arguments)
		}
	}


})();