<%@ page contentType="application/javascript;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="zk" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>
<%@ taglib uri="tld://onexas/axes" prefix="x" %>

${zk:setCWRCacheControl()}
<c:forEach items="${x:cfglist('axes.javascripts.javascript')}" var="each">
${x:loadjs(each,x:cfgboolean('axes.javascripts.[@cache]'),x:cfgboolean('axes.javascripts.[@compress]'))}
</c:forEach>
<%--overwrite the zk application name to our name --%>
var fn = function(){
	zk.appName='${x:esclabel('axes.productShortName')}';
};
zk.afterMount(fn);