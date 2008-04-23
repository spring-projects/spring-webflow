<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE webflow-upgrader [
<!ENTITY stripEl "<xsl:if test='starts-with($stripElParam,$elPrefix)'><xsl:value-of select='substring-after(substring-before($stripElParam,$elSuffix),$elPrefix)'/></xsl:if><xsl:if test='not(starts-with($stripElParam,$elPrefix))'><xsl:value-of select='$stripElParam'/></xsl:if>">
]>
<xsl:stylesheet xmlns:webflow="http://www.springframework.org/schema/webflow" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>
    
    <xsl:variable name="scopeSuffix">
        <xsl:text>Scope</xsl:text>
    </xsl:variable>
    
    <xsl:variable name="elPrefix">
        <xsl:text>${</xsl:text>
    </xsl:variable>
    
    <xsl:variable name="elSuffix">
        <xsl:text>}</xsl:text>
    </xsl:variable>
    
    <xsl:template match="comment()">
        <xsl:text>&#xA;</xsl:text>
        <xsl:comment><xsl:value-of select="."/></xsl:comment>
    </xsl:template>
    
    <xsl:template match="webflow:flow">
        <xsl:element name="flow" namespace="http://www.springframework.org/schema/webflow">
            <xsl:attribute name="schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance">
                <xsl:text>http://www.springframework.org/schema/webflow</xsl:text>
                <xsl:text> </xsl:text>
                <xsl:text>http://www.springframework.org/schema/webflow/spring-webflow-2.0.xsd</xsl:text>
            </xsl:attribute>
            <xsl:if test="webflow:start-state">
                <xsl:attribute name="start-state">
                    <xsl:value-of select="webflow:start-state/@idref"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:text>&#xA;</xsl:text>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:action">
        <xsl:element name="evaluate" namespace="http://www.springframework.org/schema/webflow">
            <xsl:attribute name="expression">
                <xsl:if test="@bean">
                    <xsl:value-of select="@bean"/>
                </xsl:if>
                <xsl:if test="@bean and @method">
                    <xsl:text>.</xsl:text>
                </xsl:if>
                <xsl:if test="@method">
                    <xsl:value-of select="@method"/>
                </xsl:if>
            </xsl:attribute>
            <xsl:if test="@name">
                <xsl:element name="attribute" namespace="http://www.springframework.org/schema/webflow">
                    <xsl:attribute name="name">
                        <xsl:text>name</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:variable name="stripElParam">
                            <xsl:value-of select="@name"/>
                        </xsl:variable>
                        &stripEl;
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:action-state">
        <xsl:element name="action-state" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@id">
                <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:attribute">
        <xsl:element name="attribute" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@name">
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@value">
                <xsl:attribute name="value">
                    <xsl:value-of select="@value"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@type">
                <xsl:attribute name="type">
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:attribute-mapper">
        <!-- bean attribute handled in subflow-state -->
        <xsl:apply-templates select="*|comment()"/>
    </xsl:template>
    
    <xsl:template match="webflow:bean-action">
        <xsl:if test="webflow:method-arguments/webflow:argument/@parameter-type">
            <xsl:text>&#xA;</xsl:text>
            <xsl:comment>
                <xsl:text> WARNING: parameter-type is no longer supported.  Use &lt;set name="temp" value="</xsl:text>
                <xsl:value-of select="webflow:method-arguments/webflow:argument/@expression"/>
                <xsl:text>" type="</xsl:text>
                <xsl:value-of select="webflow:method-arguments/webflow:argument/@parameter-type"/>
                <xsl:text>" /&gt; to convert the type before the evaluate action. </xsl:text>
            </xsl:comment>
        </xsl:if>
        <xsl:element name="evaluate" namespace="http://www.springframework.org/schema/webflow">
            <xsl:attribute name="expression">
                <xsl:value-of select="@bean"/>
                <xsl:text>.</xsl:text>
                <xsl:value-of select="@method"/>
                <xsl:if test="webflow:method-arguments">
                    <xsl:text>(</xsl:text>
                    <xsl:for-each select="webflow:method-arguments/webflow:argument">
                        <xsl:variable name="stripElParam">
                            <xsl:value-of select="@expression"/>
                        </xsl:variable>
                        &stripEl;
                        <xsl:if test="position() != count(../webflow:argument)">
                            <xsl:text>, </xsl:text>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:text>)</xsl:text>
                </xsl:if>
            </xsl:attribute>
            <xsl:if test="webflow:method-result">
                <xsl:attribute name="result">
                    <xsl:if test="webflow:method-result/@scope">
                        <xsl:value-of select="webflow:method-result/@scope"/>
                        <xsl:value-of select="$scopeSuffix"/>
                        <xsl:text>.</xsl:text>
                    </xsl:if>
                    <xsl:if test="not(webflow:method-result/@scope)">
                        <xsl:text>request</xsl:text>
                        <xsl:value-of select="$scopeSuffix"/>
                        <xsl:text>.</xsl:text>
                    </xsl:if>
                    <xsl:value-of select="webflow:method-result/@name"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@name">
                <xsl:element name="attribute" namespace="http://www.springframework.org/schema/webflow">
                    <xsl:attribute name="name">
                        <xsl:text>name</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:decision-state">
        <xsl:element name="decision-state" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@id">
                <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:end-actions">
        <xsl:element name="on-end" namespace="http://www.springframework.org/schema/webflow">
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:end-state">
        <xsl:element name="end-state" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@id">
                <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@view">
                <xsl:attribute name="view">
                    <xsl:value-of select="@view"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:entry-actions">
        <xsl:element name="on-entry" namespace="http://www.springframework.org/schema/webflow">
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:evaluate-action">
        <xsl:element name="evaluate" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@expression">
                <xsl:attribute name="expression">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@expression"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="webflow:evaluation-result">
                <xsl:attribute name="result">
                    <xsl:if test="webflow:evaluation-result/@scope">
                        <xsl:value-of select="webflow:evaluation-result/@scope"/>
                        <xsl:value-of select="$scopeSuffix"/>
                        <xsl:text>.</xsl:text>
                    </xsl:if>
                    <xsl:if test="not(webflow:evaluation-result/@scope)">
                        <xsl:text>request</xsl:text>
                        <xsl:value-of select="$scopeSuffix"/>
                        <xsl:text>.</xsl:text>
                    </xsl:if>
                    <xsl:value-of select="webflow:evaluation-result/@name"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@name">
                <xsl:element name="attribute" namespace="http://www.springframework.org/schema/webflow">
                    <xsl:attribute name="name">
                        <xsl:text>name</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:exception-handler">
        <xsl:element name="exception-handler" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@bean">
                <xsl:attribute name="bean">
                    <xsl:value-of select="@bean"/>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:exit-actions">
        <xsl:element name="on-exit" namespace="http://www.springframework.org/schema/webflow">
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:global-transitions">
        <xsl:element name="global-transitions" namespace="http://www.springframework.org/schema/webflow">
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:if">
        <xsl:element name="if" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@test">
                <xsl:attribute name="test">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@test"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@then">
                <xsl:attribute name="then">
                    <xsl:value-of select="@then"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@else">
                <xsl:attribute name="else">
                    <xsl:value-of select="@else"/>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:import">
        <xsl:element name="bean-import" namespace="http://www.springframework.org/schema/webflow">
            <xsl:attribute name="resource">
                <xsl:value-of select="@resource"/>
            </xsl:attribute>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:inline-flow">
        <xsl:element name="inline-flow" namespace="http://www.springframework.org/schema/webflow">
            <xsl:text>&#xA;</xsl:text>
            <xsl:comment> WARNING: inline-flow is no longer supported.  Create a new top level flow. </xsl:comment>
            <!-- Convert the content of the inline-flow to make pulling it out easier.  This will not validate against the schema. -->
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:input-attribute">
        <xsl:element name="input" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@name">
                <xsl:attribute name="name">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@name"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
                <xsl:if test="@scope">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@scope"/>
                        <xsl:value-of select="$scopeSuffix"/>
                        <xsl:text>.</xsl:text>
                        <xsl:variable name="stripElParam">
                            <xsl:value-of select="@name"/>
                        </xsl:variable>
                        &stripEl;
                    </xsl:attribute>
                </xsl:if>
            </xsl:if>
            <xsl:if test="@required">
                <xsl:attribute name="required">
                    <xsl:value-of select="@required"/>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:input-mapper">
        <xsl:for-each select="webflow:mapping">
            <xsl:element name="input" namespace="http://www.springframework.org/schema/webflow">
                <xsl:apply-templates select="."/>
            </xsl:element>
        </xsl:for-each>
        <xsl:apply-templates select="webflow:input-attribute"/>
        <xsl:if test="local-name(..) = 'flow'">
            <xsl:text>&#xA;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="webflow:mapping">
        <xsl:if test="(local-name(../..) = 'flow' and local-name(..) = 'input-mapper') or (local-name(../../..) = 'subflow-state' and local-name(..) = 'output-mapper')">
            <xsl:if test="@source">
                <xsl:attribute name="name">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@source"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@target">
                <xsl:attribute name="value">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@target"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@target-collection">
                <xsl:attribute name="value">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@target-collection"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
        </xsl:if>
        <xsl:if test="(local-name(../..) = 'flow' and local-name(..) = 'output-mapper') or (local-name(../../..) = 'subflow-state' and local-name(..) = 'input-mapper') or (local-name(../../..) = 'end-state' and local-name(..) = 'output-mapper')">
            <xsl:if test="@target">
                <xsl:attribute name="name">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@target"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@target-collection">
                <xsl:attribute name="name">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@target-collection"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@source">
                <xsl:attribute name="value">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@source"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
        </xsl:if>
        <xsl:if test="@to">
            <xsl:attribute name="type">
                <xsl:value-of select="@to"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="@required">
            <xsl:attribute name="required">
                <xsl:value-of select="@required"/>
            </xsl:attribute>
        </xsl:if>
        <xsl:if test="@target-collection">
            <xsl:text>&#xA;</xsl:text>
            <xsl:comment>
                <xsl:text> WARNING: target-collection is no longer supported.  This will overwrite the entire collection. </xsl:text>
            </xsl:comment>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="webflow:output-attribute">
        <xsl:element name="output" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@name">
                <xsl:attribute name="name">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@name"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
                <xsl:if test="@scope">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@scope"/>
                        <xsl:value-of select="$scopeSuffix"/>
                        <xsl:text>.</xsl:text>
                        <xsl:variable name="stripElParam">
                            <xsl:value-of select="@name"/>
                        </xsl:variable>
                        &stripEl;
                    </xsl:attribute>
                </xsl:if>
            </xsl:if>
            <xsl:if test="@required">
                <xsl:attribute name="required">
                    <xsl:value-of select="@required"/>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:output-mapper">
        <xsl:for-each select="webflow:mapping">
            <xsl:element name="output" namespace="http://www.springframework.org/schema/webflow">
                <xsl:apply-templates select="."/>
            </xsl:element>
        </xsl:for-each>
        <xsl:apply-templates select="webflow:output-attribute"/>
        <xsl:if test="local-name(..) = 'flow'">
            <xsl:text>&#xA;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="webflow:render-actions">
        <xsl:element name="on-render" namespace="http://www.springframework.org/schema/webflow">
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:set">
        <xsl:element name="set" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@attribute">
                <xsl:attribute name="name">
                    <xsl:if test="@scope">
                        <xsl:value-of select="@scope"/>
                        <xsl:value-of select="$scopeSuffix"/>
                        <xsl:text>.</xsl:text>
                    </xsl:if>
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@attribute"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@value">
                <xsl:attribute name="value">
                    <xsl:variable name="stripElParam">
                        <xsl:value-of select="@value"/>
                    </xsl:variable>
                    &stripEl;
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@name">
                <xsl:element name="attribute" namespace="http://www.springframework.org/schema/webflow">
                    <xsl:attribute name="name">
                        <xsl:text>name</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:start-actions">
        <xsl:element name="on-start" namespace="http://www.springframework.org/schema/webflow">
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:subflow-state">
        <xsl:element name="subflow-state" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@id">
                <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@flow">
                <xsl:attribute name="subflow">
                    <xsl:value-of select="@flow"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="webflow:attribute-mapper/@bean">
                <xsl:attribute name="subflow-attribute-mapper">
                    <xsl:value-of select="webflow:subflow-state/@bean"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
    <xsl:template match="webflow:transition">
        <xsl:element name="transition" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@on and @on != '*'">
                <xsl:attribute name="on">
                    <xsl:value-of select="@on"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@on-exception">
                <xsl:attribute name="on-exception">
                    <xsl:value-of select="@on-exception"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@to">
                <xsl:attribute name="to">
                    <xsl:value-of select="@to"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:value">
        <xsl:element name="value" namespace="http://www.springframework.org/schema/webflow">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="webflow:var">
        <xsl:element name="var" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@name">
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@class">
                <xsl:attribute name="class">
                    <xsl:value-of select="@class"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@bean">
                <xsl:text>&#xA;</xsl:text>
                <xsl:comment> WARNING: the bean attribute is no longer supported </xsl:comment>
            </xsl:if>
        </xsl:element>
        <xsl:if test="@scope = 'conversation'">
            <xsl:text>&#xA;</xsl:text>
            <xsl:comment> WARNING: variables are always set into flow scope </xsl:comment>
        </xsl:if>
        <xsl:if test="local-name(..) = 'flow'">
            <xsl:text>&#xA;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="webflow:view-state">
        <xsl:element name="view-state" namespace="http://www.springframework.org/schema/webflow">
            <xsl:if test="@id">
                <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@view">
                <xsl:attribute name="view">
                    <xsl:value-of select="@view"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="*|comment()"/>
        </xsl:element>
        <xsl:text>&#xA;</xsl:text>
    </xsl:template>
    
</xsl:stylesheet>
