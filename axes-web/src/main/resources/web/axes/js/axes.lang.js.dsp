<%@ page contentType="application/javascript;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="tld://onexas/axes" prefix="x" %>
<%-- dont cache lang js --%>(function(){
if(window.xlang)
	return;
var xl = {};
<c:forEach items="${x:cfglist('axes.jslabels.jslabel')}" var="each">xl['${each}']='${x:esclabel(each)}';
</c:forEach>
window.xlang = xl;
})();