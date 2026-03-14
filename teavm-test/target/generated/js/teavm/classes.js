"use strict";
(function(root,module){if(typeof define==='function'&&define.amd){define(['exports'],function(exports){module(root,exports);});}else if(typeof exports==='object'&&exports!==null&&typeof exports.nodeName!=='string'){module(global,exports);}else{module(root,root);}}(typeof self!=='undefined'?self:this,function($rt_globals,$rt_exports){var $rt_seed=2463534242;function $rt_nextId(){var x=$rt_seed;x^=x<<13;x^=x>>>17;x^=x<<5;$rt_seed=x;return x;}function $rt_compare(a,b){return a>b?1:a<b? -1:a===b?0:1;}function $rt_isInstance(obj,
cls){return obj instanceof $rt_objcls()&&!!obj.constructor.$meta&&$rt_isAssignable(obj.constructor,cls);}function $rt_isAssignable(from,to){if(from===to){return true;}if(to.$meta.item!==null){return from.$meta.item!==null&&$rt_isAssignable(from.$meta.item,to.$meta.item);}var supertypes=from.$meta.supertypes;for(var i=0;i<supertypes.length;i=i+1|0){if($rt_isAssignable(supertypes[i],to)){return true;}}return false;}function $rt_castToInterface(obj,cls){if(obj!==null&&!$rt_isInstance(obj,cls)){$rt_throwCCE();}return obj;}function $rt_castToClass(obj,
cls){if(obj!==null&&!(obj instanceof cls)){$rt_throwCCE();}return obj;}$rt_globals.Array.prototype.fill=$rt_globals.Array.prototype.fill||function(value,start,end){var len=this.length;if(!len)return this;start=start|0;var i=start<0?$rt_globals.Math.max(len+start,0):$rt_globals.Math.min(start,len);end=end===$rt_globals.undefined?len:end|0;end=end<0?$rt_globals.Math.max(len+end,0):$rt_globals.Math.min(end,len);for(;i<end;i++){this[i]=value;}return this;};function $rt_createArray(cls,sz){var data=new $rt_globals.Array(sz);data.fill(null);return new $rt_array(cls,
data);}function $rt_createArrayFromData(cls,init){return $rt_wrapArray(cls,init);}function $rt_wrapArray(cls,data){return new $rt_array(cls,data);}function $rt_createUnfilledArray(cls,sz){return new $rt_array(cls,new $rt_globals.Array(sz));}function $rt_createNumericArray(cls,nativeArray){return new $rt_array(cls,nativeArray);}var $rt_createLongArray;var $rt_createLongArrayFromData;if(typeof $rt_globals.BigInt64Array!=='function'){$rt_createLongArray=function(sz){var data=new $rt_globals.Array(sz);var arr=new $rt_array($rt_longcls(),
data);data.fill(Long_ZERO);return arr;};$rt_createLongArrayFromData=function(init){return new $rt_array($rt_longcls(),init);};}else {$rt_createLongArray=function(sz){return $rt_createNumericArray($rt_longcls(),new $rt_globals.BigInt64Array(sz));};$rt_createLongArrayFromData=function(data){var buffer=new $rt_globals.BigInt64Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_longcls(),buffer);};}function $rt_createCharArray(sz){return $rt_createNumericArray($rt_charcls(),new $rt_globals.Uint16Array(sz));}function $rt_createCharArrayFromData(data)
{var buffer=new $rt_globals.Uint16Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_charcls(),buffer);}function $rt_createByteArray(sz){return $rt_createNumericArray($rt_bytecls(),new $rt_globals.Int8Array(sz));}function $rt_createByteArrayFromData(data){var buffer=new $rt_globals.Int8Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_bytecls(),buffer);}function $rt_createShortArray(sz){return $rt_createNumericArray($rt_shortcls(),new $rt_globals.Int16Array(sz));}function $rt_createShortArrayFromData(data)
{var buffer=new $rt_globals.Int16Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_shortcls(),buffer);}function $rt_createIntArray(sz){return $rt_createNumericArray($rt_intcls(),new $rt_globals.Int32Array(sz));}function $rt_createIntArrayFromData(data){var buffer=new $rt_globals.Int32Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_intcls(),buffer);}function $rt_createBooleanArray(sz){return $rt_createNumericArray($rt_booleancls(),new $rt_globals.Int8Array(sz));}function $rt_createBooleanArrayFromData(data)
{var buffer=new $rt_globals.Int8Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_booleancls(),buffer);}function $rt_createFloatArray(sz){return $rt_createNumericArray($rt_floatcls(),new $rt_globals.Float32Array(sz));}function $rt_createFloatArrayFromData(data){var buffer=new $rt_globals.Float32Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_floatcls(),buffer);}function $rt_createDoubleArray(sz){return $rt_createNumericArray($rt_doublecls(),new $rt_globals.Float64Array(sz));}function $rt_createDoubleArrayFromData(data)
{var buffer=new $rt_globals.Float64Array(data.length);buffer.set(data);return $rt_createNumericArray($rt_doublecls(),buffer);}function $rt_arraycls(cls){var result=cls.$array;if(result===null){var arraycls={};var name="["+cls.$meta.binaryName;arraycls.$meta={item:cls,supertypes:[$rt_objcls()],primitive:false,superclass:$rt_objcls(),name:name,binaryName:name,enum:false,simpleName:null,declaringClass:null,enclosingClass:null};arraycls.classObject=null;arraycls.$array=null;result=arraycls;cls.$array=arraycls;}return result;}function $rt_createcls()
{return {$array:null,classObject:null,$meta:{supertypes:[],superclass:null}};}function $rt_createPrimitiveCls(name,binaryName){var cls=$rt_createcls();cls.$meta.primitive=true;cls.$meta.name=name;cls.$meta.binaryName=binaryName;cls.$meta.enum=false;cls.$meta.item=null;cls.$meta.simpleName=null;cls.$meta.declaringClass=null;cls.$meta.enclosingClass=null;return cls;}var $rt_booleanclsCache=null;function $rt_booleancls(){if($rt_booleanclsCache===null){$rt_booleanclsCache=$rt_createPrimitiveCls("boolean","Z");}return $rt_booleanclsCache;}var $rt_charclsCache
=null;function $rt_charcls(){if($rt_charclsCache===null){$rt_charclsCache=$rt_createPrimitiveCls("char","C");}return $rt_charclsCache;}var $rt_byteclsCache=null;function $rt_bytecls(){if($rt_byteclsCache===null){$rt_byteclsCache=$rt_createPrimitiveCls("byte","B");}return $rt_byteclsCache;}var $rt_shortclsCache=null;function $rt_shortcls(){if($rt_shortclsCache===null){$rt_shortclsCache=$rt_createPrimitiveCls("short","S");}return $rt_shortclsCache;}var $rt_intclsCache=null;function $rt_intcls(){if($rt_intclsCache
===null){$rt_intclsCache=$rt_createPrimitiveCls("int","I");}return $rt_intclsCache;}var $rt_longclsCache=null;function $rt_longcls(){if($rt_longclsCache===null){$rt_longclsCache=$rt_createPrimitiveCls("long","J");}return $rt_longclsCache;}var $rt_floatclsCache=null;function $rt_floatcls(){if($rt_floatclsCache===null){$rt_floatclsCache=$rt_createPrimitiveCls("float","F");}return $rt_floatclsCache;}var $rt_doubleclsCache=null;function $rt_doublecls(){if($rt_doubleclsCache===null){$rt_doubleclsCache=$rt_createPrimitiveCls("double",
"D");}return $rt_doubleclsCache;}var $rt_voidclsCache=null;function $rt_voidcls(){if($rt_voidclsCache===null){$rt_voidclsCache=$rt_createPrimitiveCls("void","V");}return $rt_voidclsCache;}function $rt_throw(ex){throw $rt_exception(ex);}var $rt_javaExceptionProp=$rt_globals.Symbol("javaException");function $rt_exception(ex){var err=ex.$jsException;if(!err){var javaCause=$rt_throwableCause(ex);var jsCause=javaCause!==null?javaCause.$jsException:$rt_globals.undefined;var cause=typeof jsCause==="object"?{cause:
jsCause}:$rt_globals.undefined;err=new JavaError("Java exception thrown",cause);if(typeof $rt_globals.Error.captureStackTrace==="function"){$rt_globals.Error.captureStackTrace(err);}err[$rt_javaExceptionProp]=ex;ex.$jsException=err;$rt_fillStack(err,ex);}return err;}function $rt_fillStack(err,ex){if(typeof $rt_decodeStack==="function"&&err.stack){var stack=$rt_decodeStack(err.stack);var javaStack=$rt_createArray($rt_stecls(),stack.length);var elem;var noStack=false;for(var i=0;i<stack.length;++i){var element
=stack[i];elem=$rt_createStackElement($rt_str(element.className),$rt_str(element.methodName),$rt_str(element.fileName),element.lineNumber);if(elem==null){noStack=true;break;}javaStack.data[i]=elem;}if(!noStack){$rt_setStack(ex,javaStack);}}}function $rt_createMultiArray(cls,dimensions){var first=0;for(var i=dimensions.length -1;i>=0;i=i -1|0){if(dimensions[i]===0){first=i;break;}}if(first>0){for(i=0;i<first;i=i+1|0){cls=$rt_arraycls(cls);}if(first===dimensions.length -1){return $rt_createArray(cls,dimensions[first]);}}var arrays
=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,first));var firstDim=dimensions[first]|0;for(i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createArray(cls,firstDim);}return $rt_createMultiArrayImpl(cls,arrays,dimensions,first);}function $rt_createByteMultiArray(dimensions){var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if(arrays.length===0){return $rt_createMultiArray($rt_bytecls(),dimensions);}var firstDim=dimensions[0]|0;for(var i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createByteArray(firstDim);}return $rt_createMultiArrayImpl($rt_bytecls(),
arrays,dimensions);}function $rt_createCharMultiArray(dimensions){var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if(arrays.length===0){return $rt_createMultiArray($rt_charcls(),dimensions);}var firstDim=dimensions[0]|0;for(var i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createCharArray(firstDim);}return $rt_createMultiArrayImpl($rt_charcls(),arrays,dimensions,0);}function $rt_createBooleanMultiArray(dimensions){var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if
(arrays.length===0){return $rt_createMultiArray($rt_booleancls(),dimensions);}var firstDim=dimensions[0]|0;for(var i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createBooleanArray(firstDim);}return $rt_createMultiArrayImpl($rt_booleancls(),arrays,dimensions,0);}function $rt_createShortMultiArray(dimensions){var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if(arrays.length===0){return $rt_createMultiArray($rt_shortcls(),dimensions);}var firstDim=dimensions[0]|0;for(var i=0;i<arrays.length;i
=i+1|0){arrays[i]=$rt_createShortArray(firstDim);}return $rt_createMultiArrayImpl($rt_shortcls(),arrays,dimensions,0);}function $rt_createIntMultiArray(dimensions){var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if(arrays.length===0){return $rt_createMultiArray($rt_intcls(),dimensions);}var firstDim=dimensions[0]|0;for(var i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createIntArray(firstDim);}return $rt_createMultiArrayImpl($rt_intcls(),arrays,dimensions,0);}function $rt_createLongMultiArray(dimensions)
{var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if(arrays.length===0){return $rt_createMultiArray($rt_longcls(),dimensions);}var firstDim=dimensions[0]|0;for(var i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createLongArray(firstDim);}return $rt_createMultiArrayImpl($rt_longcls(),arrays,dimensions,0);}function $rt_createFloatMultiArray(dimensions){var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if(arrays.length===0){return $rt_createMultiArray($rt_floatcls(),dimensions);}var firstDim
=dimensions[0]|0;for(var i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createFloatArray(firstDim);}return $rt_createMultiArrayImpl($rt_floatcls(),arrays,dimensions,0);}function $rt_createDoubleMultiArray(dimensions){var arrays=new $rt_globals.Array($rt_primitiveArrayCount(dimensions,0));if(arrays.length===0){return $rt_createMultiArray($rt_doublecls(),dimensions);}var firstDim=dimensions[0]|0;for(var i=0;i<arrays.length;i=i+1|0){arrays[i]=$rt_createDoubleArray(firstDim);}return $rt_createMultiArrayImpl($rt_doublecls(),
arrays,dimensions,0);}function $rt_primitiveArrayCount(dimensions,start){var val=dimensions[start+1]|0;for(var i=start+2;i<dimensions.length;i=i+1|0){val=val*(dimensions[i]|0)|0;if(val===0){break;}}return val;}function $rt_createMultiArrayImpl(cls,arrays,dimensions,start){var limit=arrays.length;for(var i=start+1|0;i<dimensions.length;i=i+1|0){cls=$rt_arraycls(cls);var dim=dimensions[i];var index=0;var packedIndex=0;while(index<limit){var arr=$rt_createUnfilledArray(cls,dim);for(var j=0;j<dim;j=j+1|0){arr.data[j]
=arrays[index];index=index+1|0;}arrays[packedIndex]=arr;packedIndex=packedIndex+1|0;}limit=packedIndex;}return arrays[0];}function $rt_assertNotNaN(value){if(typeof value==='number'&&$rt_globals.isNaN(value)){throw "NaN";}return value;}function $rt_createOutputFunction(printFunction){var buffer="";var utf8Buffer=0;var utf8Remaining=0;function putCodePoint(ch){if(ch===0xA){printFunction(buffer);buffer="";}else if(ch<0x10000){buffer+=$rt_globals.String.fromCharCode(ch);}else {ch=ch -0x10000|0;var hi=(ch>>10)+
0xD800;var lo=(ch&0x3FF)+0xDC00;buffer+=$rt_globals.String.fromCharCode(hi,lo);}}return function(ch){if((ch&0x80)===0){putCodePoint(ch);}else if((ch&0xC0)===0x80){if(utf8Buffer>0){utf8Remaining<<=6;utf8Remaining|=ch&0x3F;if( --utf8Buffer===0){putCodePoint(utf8Remaining);}}}else if((ch&0xE0)===0xC0){utf8Remaining=ch&0x1F;utf8Buffer=1;}else if((ch&0xF0)===0xE0){utf8Remaining=ch&0x0F;utf8Buffer=2;}else if((ch&0xF8)===0xF0){utf8Remaining=ch&0x07;utf8Buffer=3;}};}var $rt_putStdout=typeof $rt_putStdoutCustom==="function"
?$rt_putStdoutCustom:typeof $rt_globals.console==="object"?$rt_createOutputFunction(function(msg){$rt_globals.console.info(msg);}):function(){};var $rt_putStderr=typeof $rt_putStderrCustom==="function"?$rt_putStderrCustom:typeof $rt_globals.console==="object"?$rt_createOutputFunction(function(msg){$rt_globals.console.error(msg);}):function(){};var $rt_packageData=null;function $rt_packages(data){var i=0;var packages=new $rt_globals.Array(data.length);for(var j=0;j<data.length;++j){var prefixIndex=data[i++];var prefix
=prefixIndex>=0?packages[prefixIndex]:"";packages[j]=prefix+data[i++]+".";}$rt_packageData=packages;}function $rt_metadata(data){var packages=$rt_packageData;var i=0;while(i<data.length){var cls=data[i++];cls.$meta={};var m=cls.$meta;var className=data[i++];m.name=className!==0?className:null;if(m.name!==null){var packageIndex=data[i++];if(packageIndex>=0){m.name=packages[packageIndex]+m.name;}}m.binaryName="L"+m.name+";";var superclass=data[i++];m.superclass=superclass!==0?superclass:null;m.supertypes=data[i++];if
(m.superclass){m.supertypes.push(m.superclass);cls.prototype=$rt_globals.Object.create(m.superclass.prototype);}else {cls.prototype={};}var flags=data[i++];m.enum=(flags&8)!==0;m.flags=flags;m.primitive=false;m.item=null;cls.prototype.constructor=cls;cls.classObject=null;m.accessLevel=data[i++];var innerClassInfo=data[i++];if(innerClassInfo===0){m.simpleName=null;m.declaringClass=null;m.enclosingClass=null;}else {var enclosingClass=innerClassInfo[0];m.enclosingClass=enclosingClass!==0?enclosingClass:null;var declaringClass
=innerClassInfo[1];m.declaringClass=declaringClass!==0?declaringClass:null;var simpleName=innerClassInfo[2];m.simpleName=simpleName!==0?simpleName:null;}var clinit=data[i++];cls.$clinit=clinit!==0?clinit:function(){};var virtualMethods=data[i++];if(virtualMethods!==0){for(var j=0;j<virtualMethods.length;j+=2){var name=virtualMethods[j];var func=virtualMethods[j+1];if(typeof name==='string'){name=[name];}for(var k=0;k<name.length;++k){cls.prototype[name[k]]=func;}}}cls.$array=null;}}function $rt_wrapFunction0(f)
{return function(){return f(this);};}function $rt_wrapFunction1(f){return function(p1){return f(this,p1);};}function $rt_wrapFunction2(f){return function(p1,p2){return f(this,p1,p2);};}function $rt_wrapFunction3(f){return function(p1,p2,p3){return f(this,p1,p2,p3,p3);};}function $rt_wrapFunction4(f){return function(p1,p2,p3,p4){return f(this,p1,p2,p3,p4);};}function $rt_threadStarter(f){return function(){var args=$rt_globals.Array.prototype.slice.apply(arguments);$rt_startThread(function(){f.apply(this,args);});};}function $rt_mainStarter(f)
{return function(args,callback){if(!args){args=[];}var javaArgs=$rt_createArray($rt_objcls(),args.length);for(var i=0;i<args.length;++i){javaArgs.data[i]=$rt_str(args[i]);}$rt_startThread(function(){f.call(null,javaArgs);},callback);};}var $rt_stringPool_instance;function $rt_stringPool(strings){$rt_stringPool_instance=new $rt_globals.Array(strings.length);for(var i=0;i<strings.length;++i){$rt_stringPool_instance[i]=$rt_intern($rt_str(strings[i]));}}function $rt_s(index){return $rt_stringPool_instance[index];}function $rt_eraseClinit(target)
{return target.$clinit=function(){};}var $rt_numberConversionBuffer=new $rt_globals.ArrayBuffer(16);var $rt_numberConversionView=new $rt_globals.DataView($rt_numberConversionBuffer);var $rt_numberConversionFloatArray=new $rt_globals.Float32Array($rt_numberConversionBuffer);var $rt_numberConversionDoubleArray=new $rt_globals.Float64Array($rt_numberConversionBuffer);var $rt_numberConversionIntArray=new $rt_globals.Int32Array($rt_numberConversionBuffer);var $rt_doubleToRawLongBits;var $rt_longBitsToDouble;if(typeof $rt_globals.BigInt
!=='function'){$rt_doubleToRawLongBits=function(n){$rt_numberConversionView.setFloat64(0,n,true);return new Long($rt_numberConversionView.getInt32(0,true),$rt_numberConversionView.getInt32(4,true));};$rt_longBitsToDouble=function(n){$rt_numberConversionView.setInt32(0,n.lo,true);$rt_numberConversionView.setInt32(4,n.hi,true);return $rt_numberConversionView.getFloat64(0,true);};}else if(typeof $rt_globals.BigInt64Array!=='function'){$rt_doubleToRawLongBits=function(n){$rt_numberConversionView.setFloat64(0,n,
true);var lo=$rt_numberConversionView.getInt32(0,true);var hi=$rt_numberConversionView.getInt32(4,true);return $rt_globals.BigInt.asIntN(64,$rt_globals.BigInt.asUintN(32,$rt_globals.BigInt(lo))|$rt_globals.BigInt(hi)<<$rt_globals.BigInt(32));};$rt_longBitsToDouble=function(n){$rt_numberConversionView.setFloat64(0,n,true);var lo=$rt_numberConversionView.getInt32(0,true);var hi=$rt_numberConversionView.getInt32(4,true);return $rt_globals.BigInt.asIntN(64,$rt_globals.BigInt.asUintN(32,$rt_globals.BigInt(lo))|$rt_globals.BigInt(hi)
<<$rt_globals.BigInt(32));};}else {var $rt_numberConversionLongArray=new $rt_globals.BigInt64Array($rt_numberConversionBuffer);$rt_doubleToRawLongBits=function(n){$rt_numberConversionDoubleArray[0]=n;return $rt_numberConversionLongArray[0];};$rt_longBitsToDouble=function(n){$rt_numberConversionLongArray[0]=n;return $rt_numberConversionDoubleArray[0];};}function $rt_floatToRawIntBits(n){$rt_numberConversionFloatArray[0]=n;return $rt_numberConversionIntArray[0];}function $rt_intBitsToFloat(n){$rt_numberConversionIntArray[0]
=n;return $rt_numberConversionFloatArray[0];}function $rt_equalDoubles(a,b){if(a!==a){return b!==b;}$rt_numberConversionDoubleArray[0]=a;$rt_numberConversionDoubleArray[1]=b;return $rt_numberConversionIntArray[0]===$rt_numberConversionIntArray[2]&&$rt_numberConversionIntArray[1]===$rt_numberConversionIntArray[3];}var JavaError;if(typeof $rt_globals.Reflect==='object'){var defaultMessage=$rt_globals.Symbol("defaultMessage");JavaError=function JavaError(message,cause){var self=$rt_globals.Reflect.construct($rt_globals.Error,
[$rt_globals.undefined,cause],JavaError);$rt_globals.Object.setPrototypeOf(self,JavaError.prototype);self[defaultMessage]=message;return self;};JavaError.prototype=$rt_globals.Object.create($rt_globals.Error.prototype,{constructor:{configurable:true,writable:true,value:JavaError},message:{get:function(){try {var javaException=this[$rt_javaExceptionProp];if(typeof javaException==='object'){var javaMessage=$rt_throwableMessage(javaException);if(typeof javaMessage==="object"){return javaMessage!==null?javaMessage.toString()
:null;}}return this[defaultMessage];}catch(e){return "Exception occurred trying to extract Java exception message: "+e;}}}});}else {JavaError=$rt_globals.Error;}function $rt_javaException(e){return e instanceof $rt_globals.Error&&typeof e[$rt_javaExceptionProp]==='object'?e[$rt_javaExceptionProp]:null;}function $rt_jsException(e){return typeof e.$jsException==='object'?e.$jsException:null;}function $rt_wrapException(err){var ex=err[$rt_javaExceptionProp];if(!ex){ex=$rt_createException($rt_str("(JavaScript) "
+err.toString()));err[$rt_javaExceptionProp]=ex;ex.$jsException=err;$rt_fillStack(err,ex);}return ex;}function $dbg_class(obj){var cls=obj.constructor;var arrayDegree=0;while(cls.$meta&&cls.$meta.item){++arrayDegree;cls=cls.$meta.item;}var clsName="";if(cls===$rt_booleancls()){clsName="boolean";}else if(cls===$rt_bytecls()){clsName="byte";}else if(cls===$rt_shortcls()){clsName="short";}else if(cls===$rt_charcls()){clsName="char";}else if(cls===$rt_intcls()){clsName="int";}else if(cls===$rt_longcls()){clsName
="long";}else if(cls===$rt_floatcls()){clsName="float";}else if(cls===$rt_doublecls()){clsName="double";}else {clsName=cls.$meta?cls.$meta.name||"a/"+cls.name:"@"+cls.name;}while(arrayDegree-->0){clsName+="[]";}return clsName;}function Long(lo,hi){this.lo=lo|0;this.hi=hi|0;}Long.prototype.__teavm_class__=function(){return "long";};function Long_isPositive(a){return (a.hi&0x80000000)===0;}function Long_isNegative(a){return (a.hi&0x80000000)!==0;}var Long_MAX_NORMAL=1<<18;var Long_ZERO;var Long_create;var Long_fromInt;var Long_fromNumber;var Long_toNumber;var Long_hi;var Long_lo;if
(typeof $rt_globals.BigInt!=="function"){Long.prototype.toString=function(){var result=[];var n=this;var positive=Long_isPositive(n);if(!positive){n=Long_neg(n);}var radix=new Long(10,0);do {var divRem=Long_divRem(n,radix);result.push($rt_globals.String.fromCharCode(48+divRem[1].lo));n=divRem[0];}while(n.lo!==0||n.hi!==0);result=(result.reverse()).join('');return positive?result:"-"+result;};Long.prototype.valueOf=function(){return Long_toNumber(this);};Long_ZERO=new Long(0,0);Long_fromInt=function(val){return new Long(val,
 -(val<0)|0);};Long_fromNumber=function(val){if(val>=0){return new Long(val|0,val/0x100000000|0);}else {return Long_neg(new Long( -val|0, -val/0x100000000|0));}};Long_create=function(lo,hi){return new Long(lo,hi);};Long_toNumber=function(val){return 0x100000000*val.hi+(val.lo>>>0);};Long_hi=function(val){return val.hi;};Long_lo=function(val){return val.lo;};}else {Long_ZERO=$rt_globals.BigInt(0);Long_create=function(lo,hi){return $rt_globals.BigInt.asIntN(64,$rt_globals.BigInt.asUintN(64,$rt_globals.BigInt(lo))
|$rt_globals.BigInt.asUintN(64,$rt_globals.BigInt(hi)<<$rt_globals.BigInt(32)));};Long_fromInt=function(val){return $rt_globals.BigInt.asIntN(64,$rt_globals.BigInt(val|0));};Long_fromNumber=function(val){return $rt_globals.BigInt.asIntN(64,$rt_globals.BigInt(val>=0?$rt_globals.Math.floor(val):$rt_globals.Math.ceil(val)));};Long_toNumber=function(val){return $rt_globals.Number(val);};Long_hi=function(val){return $rt_globals.Number($rt_globals.BigInt.asIntN(64,val>>$rt_globals.BigInt(32)))|0;};Long_lo=function(val)
{return $rt_globals.Number($rt_globals.BigInt.asIntN(32,val))|0;};}var $rt_imul=$rt_globals.Math.imul||function(a,b){var ah=a>>>16&0xFFFF;var al=a&0xFFFF;var bh=b>>>16&0xFFFF;var bl=b&0xFFFF;return al*bl+(ah*bl+al*bh<<16>>>0)|0;};var $rt_udiv=function(a,b){return (a>>>0)/(b>>>0)>>>0;};var $rt_umod=function(a,b){return (a>>>0)%(b>>>0)>>>0;};var $rt_ucmp=function(a,b){a>>>=0;b>>>=0;return a<b? -1:a>b?1:0;};function $rt_checkBounds(index,array){if(index<0||index>=array.length){$rt_throwAIOOBE();}return index;}function $rt_checkUpperBound(index,
array){if(index>=array.length){$rt_throwAIOOBE();}return index;}function $rt_checkLowerBound(index){if(index<0){$rt_throwAIOOBE();}return index;}function $rt_classWithoutFields(superclass){if(superclass===0){return function(){};}if(superclass===void 0){superclass=$rt_objcls();}return function(){superclass.call(this);};}function $rt_setCloneMethod(target, f){target.be=f;}
function $rt_cls(cls){return Cj(cls);}
function $rt_str(str) {if (str === null) {return null;}var characters = $rt_createCharArray(str.length);var charsBuffer = characters.data;for (var i = 0; i < str.length; i = (i + 1) | 0) {charsBuffer[i] = str.charCodeAt(i) & 0xFFFF;}return C2(characters);}
function $rt_ustr(str) {if (str === null) {return null;}var data = str.b.data;var result = "";for (var i = 0; i < data.length; i = (i + 1) | 0) {result += String.fromCharCode(data[i]);}return result;}
function $rt_objcls() { return B; }
function $rt_stecls(){return B;}
function $rt_throwableMessage(t){return CT(t);}
function $rt_throwableCause(t){return CX(t);}
function $rt_nullCheck(val) {if (val === null) {$rt_throw(Df());}return val;}
function $rt_intern(str) {return str;}function $rt_getThread(){return null;}
function $rt_setThread(t){}
function $rt_createException(message){return Dg(message);}
function $rt_createStackElement(className,methodName,fileName,lineNumber){return null;}
function $rt_setStack(e,stack){}
function $rt_throwAIOOBE(){}
function $rt_throwCCE(){}
var A=Object.create(null);
var O=$rt_throw;var Dh=$rt_compare;var Di=$rt_nullCheck;var B2=$rt_cls;var C3=$rt_createArray;var Dj=$rt_isInstance;var Dk=$rt_nativeThread;var Dl=$rt_suspending;var Dm=$rt_resuming;var Dn=$rt_invalidPointer;var D=$rt_s;var BE=$rt_eraseClinit;var Do=$rt_imul;var Dp=$rt_wrapException;var Dq=$rt_checkBounds;var Dr=$rt_checkUpperBound;var Ds=$rt_checkLowerBound;var Dt=$rt_wrapFunction0;var Du=$rt_wrapFunction1;var Dv=$rt_wrapFunction2;var Dw=$rt_wrapFunction3;var Dx=$rt_wrapFunction4;var C=$rt_classWithoutFields;var Dy
=$rt_createArrayFromData;var Dz=$rt_createCharArrayFromData;var DA=$rt_createByteArrayFromData;var DB=$rt_createShortArrayFromData;var DC=$rt_createIntArrayFromData;var DD=$rt_createBooleanArrayFromData;var DE=$rt_createFloatArrayFromData;var DF=$rt_createDoubleArrayFromData;var DG=$rt_createLongArrayFromData;var DH=$rt_createBooleanArray;var DI=$rt_createByteArray;var DJ=$rt_createShortArray;var Bf=$rt_createCharArray;var DK=$rt_createIntArray;var DL=$rt_createLongArray;var DM=$rt_createFloatArray;var DN=$rt_createDoubleArray;var Dh
=$rt_compare;var DO=$rt_castToClass;var DP=$rt_castToInterface;var DQ=$rt_equalDoubles;var DR=Long_toNumber;var DS=Long_fromInt;var DT=Long_fromNumber;var DU=Long_create;var DV=Long_ZERO;var DW=Long_hi;var DX=Long_lo;
function B(){this.$id$=0;}
function Bx(a){return Cj(a.constructor);}
function CR(a){var b,c,d,e,f,g,h,i,j,k,l;b=a;if(!b.$id$){c=$rt_nextId();b.$id$=c;}d=a.$id$;if(!d)e=D(0);else{if(!d)f=32;else{g=0;f=d>>>16|0;if(f)g=16;else f=d;h=f>>>8|0;if(!h)h=f;else g=g|8;i=h>>>4|0;if(!i)i=h;else g=g|4;h=i>>>2|0;if(!h)h=i;else g=g|2;if(h>>>1|0)g=g|1;f=(32-g|0)-1|0;}j=(((32-f|0)+4|0)-1|0)/4|0;k=Bf(j);l=k.data;j=(j-1|0)*4|0;f=0;while(j>=0){g=f+1|0;h=(d>>>j|0)&15;l[f]=h>=0&&h<16?(h<10?(48+h|0)&65535:((97+h|0)-10|0)&65535):0;j=j-4|0;f=g;}e=C2(k);}b=new BM;b.a=Bf(16);BU(BU(b,D(1)),e);return C0(b.a,
0,b.e);}
var B_=C();
function Da(b){var c,d,e;Ch();Cb();c=$rt_globals.window.document;d=c.createElement("div");e=c.createTextNode("TeaVM generated element");d.appendChild(e);c.body.appendChild(d);}
var Bu=C(0);
var BD=C(0);
function BC(){B.call(this);this.g=null;}
function Cj(b){var c,d;if(b===null)return null;c=b.classObject;if(c===null){c=new BC;c.g=b;d=c;b.classObject=d;}return c;}
function X(a){return a.g.$meta.primitive?1:0;}
function BH(a){return Cj(a.g.$meta.item);}
var Ce=C();
function Cg(b,c){var name='jso$functor$'+c;if(!b[name]){var fn=function(){return b[c].apply(b,arguments);};b[name]=function(){return fn;};}return b[name]();}
function Bs(b,c){if(typeof b!=="function")return b;var result={};result[c]=b;return result;}
var Cp=C();
function Cn(b,c){var d,e;if(b===c)return 1;d=b.$meta.supertypes;e=0;while(e<d.length){if(Cn(d[e],c))return 1;e=e+1|0;}return 0;}
function P(){var a=this;B.call(a);a.m=null;a.t=null;a.o=0;a.n=0;}
function DY(a){var b=new P();Ba(b,a);return b;}
function Ba(a,b){a.o=1;a.n=1;a.m=b;}
function CS(a){return a;}
function CT(a){return a.m;}
function CX(a){var b;b=a.t;if(b===a)b=null;return b;}
var Br=C(P);
var I=C(Br);
function DZ(){var a=new I();L(a);return a;}
function Dg(a){var b=new I();CD(b,a);return b;}
function L(a){a.o=1;a.n=1;}
function CD(a,b){Ba(a,b);}
var Cv=C(I);
var V=C(0);
var Y=C(0);
var Bi=C(0);
function N(){var a=this;B.call(a);a.b=null;a.f=0;}
var D0=null;var D1=null;var D2=null;function Cm(){Cm=BE(N);CM();}
function C2(a){var b=new N();B5(b,a);return b;}
function C0(a,b,c){var d=new N();BY(d,a,b,c);return d;}
function B5(a,b){Cm();BY(a,b,0,b.data.length);}
function BY(a,b,c,d){var e,f,g,h,i,j,k,l,m,n;Cm();e=Bf(d);a.b=e;if(b===null){f=new BI;Ba(f,D(2));O(f);}if(c>=0&&d>=0&&(c+d|0)<=Bp(b)&&(0+d|0)<=Bp(e)){a:{b:{c:{if(b!==e){g=BH(Bx(b));f=BH(Bx(e));if(g!==null&&f!==null){if(g===f)break c;if(!X(g)&&!X(f)){h=b;i=0;j=c;while(i<d){k=h.data;l=j+1|0;m=k[j];n=f.g;if(!(m!==null&&!(typeof m.constructor.$meta==='undefined'?1:0)&&Cn(m.constructor,n)?1:0)){Bd(b,c,e,0,i);g=new Z;L(g);O(g);}i=i+1|0;j=l;}Bd(b,c,e,0,d);break a;}if(!X(g))break b;if(X(f))break c;else break b;}g=new Z;L(g);O(g);}}Bd(b,
c,e,0,d);break a;}g=new Z;L(g);O(g);}return;}g=new T;L(g);O(g);}
function Bq(a,b){var c,d;if(b>=0){c=a.b.data;if(b<c.length)return c[b];}d=new Bo;L(d);O(d);}
function M(a){return a.b.data.length;}
function U(a,b){var c;if(a===b)return 1;if(!(b instanceof N))return 0;if(M(b)!=M(a))return 0;c=0;while(c<M(b)){if(Bq(a,c)!=Bq(b,c))return 0;c=c+1|0;}return 1;}
function CQ(a){var b,c,d,e;a:{if(!a.f){b=a.b.data;c=b.length;d=0;while(true){if(d>=c)break a;e=b[d];a.f=(31*a.f|0)+e|0;d=d+1|0;}}}return a.f;}
function CM(){var b,c;b=Bf(0);D0=b;c=new N;Cm();c.b=b;D1=c;D2=new B0;}
var Bj=C(P);
var S=C(Bj);
var B6=C(S);
var Be=C();
var BO=C(Be);
var D3=null;function Ch(){D3=B2($rt_intcls());}
function Bl(){var a=this;B.call(a);a.a=null;a.e=0;}
function B8(a,b){var c,d,e,f,g;c=a.a.data.length;if(c>=b)return;d=c>=1073741823?2147483647:Ci(b,Ci(c*2|0,5));e=a.a.data;f=Bf(d);b=e.length;if(d<b)b=d;g=f.data;c=0;while(c<b){g[c]=e[c];c=c+1|0;}a.a=f;}
var B1=C(0);
var BM=C(Bl);
function BU(a,b){var c,d,e,f,g;c=a.e;if(b===null)b=D(3);if(c>=0&&c<=c){if(!(b.b.data.length?0:1)){B8(a,c+M(b)|0);d=a.e-1|0;while(d>=c){a.a.data[d+M(b)|0]=a.a.data[d];d=d+(-1)|0;}a.e=a.e+M(b)|0;e=0;while(e<M(b)){f=a.a.data;g=c+1|0;f[c]=Bq(b,e);e=e+1|0;c=g;}}return a;}b=new Bo;Cu(b);O(b);}
var R=C(S);
var Cs=C(R);
function D4(a){var b=new Cs();CV(b,a);return b;}
function CV(a,b){Ba(a,b);}
var Cd=C(R);
function D5(a){var b=new Cd();CE(b,a);return b;}
function CE(a,b){Ba(a,b);}
var H=C(0);
var Bw=C(0);
var BA=C(0);
var K=C(0);
var B3=C(0);
var Cc=C();
function C4(b,c,d,e,f){if(c>=0&&e>=0&&f>=0&&(c+f|0)<=Bp(b)&&(e+f|0)<=Bp(d)){Bd(b,c,d,e,f);return;}b=new T;L(b);O(b);}
function Bd(b,c,d,e,f){if(f===0){return;}else if(typeof b.data.buffer!=='undefined'){d.data.set(b.data.subarray(c,c+f),e);}else if (b !== d || e < c) {
for (var i = 0; i < f; i = (i + 1) | 0) {d.data[e++] = b.data[c++];}}else{c = (c + f) | 0;e = (e + f) | 0;for (var i = 0; i < f; i = (i + 1) | 0) {d.data[--e] = b.data[--c];}}}
var Cr=C();
var BS=C(0);
var BP=C(0);
var BF=C(0);
var BN=C(0);
var BJ=C(0);
var Bv=C(0);
var BX=C(0);
var By=C(0);
var Cf=C();
function Cy(a,b){b=a.E(b);Bk();return b===null?null:b instanceof $rt_objcls()&&b instanceof J?Bg(b):b;}
function CK(a,b,c){a.K($rt_str(b),Bs(c,"handleEvent"));}
function CY(a,b,c){a.bd($rt_str(b),Bs(c,"handleEvent"));}
function CW(a,b,c,d){a.R($rt_str(b),Bs(c,"handleEvent"),d?1:0);}
function CA(a,b){return !!a.Q(b);}
function Cz(a){return a.bb();}
function CI(a,b,c,d){a.S($rt_str(b),Bs(c,"handleEvent"),d?1:0);}
var BT=C(0);
var B0=C();
var T=C(I);
function D6(){var a=new T();Cu(a);return a;}
function Cu(a){L(a);}
var B7=C();
function Bp(b){if (b === null || b.constructor.$meta.item === undefined) {$rt_throw(D7());}return b.data.length;}
var BI=C(I);
var Z=C(I);
var Bb=C();
var D8=null;var D9=null;function Cb(){D8=B2($rt_charcls());D9=C3(Bb,128);}
function J(){B.call(this);this.l=null;}
var D$=null;var D_=null;var Ea=null;var Eb=null;var Ec=null;var Ed=null;var Ee=null;function Bk(){Bk=BE(J);CB();}
function Bn(a){var b=new J();Cl(b,a);return b;}
function Cl(a,b){Bk();a.l=b;}
function B9(b){var c,d,e,f,g,h,i;Bk();if(b===null)return null;c=b;d=$rt_str(typeof c);e=!U(d,D(4))&&!U(d,D(5))?0:1;if(e&&b[Ef]===true)return b;b=D_;if(b!==null){if(e){f=b.get(c);g=(typeof f==='undefined'?1:0)?void 0:f.deref();if(!(typeof g==='undefined'?1:0))return g;h=Bn(c);D_.set(c,new $rt_globals.WeakRef(h));return h;}if(U(d,D(6))){f=Ea.get(c);g=(typeof f==='undefined'?1:0)?void 0:f.deref();if(!(typeof g==='undefined'?1:0))return g;h=Bn(c);i=h;Ea.set(c,new $rt_globals.WeakRef(i));BQ(Ed,i,c);return h;}if(U(d,
D(7))){f=Eb.get(c);g=(typeof f==='undefined'?1:0)?void 0:f.deref();if(!(typeof g==='undefined'?1:0))return g;h=Bn(c);i=h;Eb.set(c,new $rt_globals.WeakRef(i));BQ(Ee,i,c);return h;}if(U(d,D(8))){f=Ec;g=f===null?void 0:f.deref();if(!(typeof g==='undefined'?1:0))return g;h=Bn(c);Ec=new $rt_globals.WeakRef(h);return h;}}return Bn(c);}
function Bg(b){Bk();if(b===null)return null;return !(b[Ef]===true)?b.l:b;}
function BK(b){Bk();if(b===null)return null;return b instanceof $rt_objcls()?b:B9(b);}
function CB(){D$=new $rt_globals.WeakMap();D_=!(typeof $rt_globals.WeakRef!=='undefined'?1:0)?null:new $rt_globals.WeakMap();Ea=!(typeof $rt_globals.WeakRef!=='undefined'?1:0)?null:new $rt_globals.Map();Eb=!(typeof $rt_globals.WeakRef!=='undefined'?1:0)?null:new $rt_globals.Map();Ed=Ea===null?null:new $rt_globals.FinalizationRegistry(Cg(new BW,"accept"));Ee=Eb===null?null:new $rt_globals.FinalizationRegistry(Cg(new BV,"accept"));}
function BQ(b,c,d){return b.register(c,d);}
var Ck=C();
var B$=C();
var Cq=C();
var Bh=C(0);
var BW=C();
function CO(a,b){var c;b=BK(b);c=Ea;b=Bg(b);c.delete(b);}
var B4=C();
var BV=C();
function CL(a,b){var c;b=BK(b);c=Eb;b=Bg(b);c.delete(b);}
var Cw=C();
var Bo=C(T);
var Ca=C();
function Ci(b,c){if(b>c)c=b;return c;}
var Cx=C();
$rt_packages([]);
$rt_metadata([B,0,0,[],0,3,0,0,0,B_,0,B,[],0,3,0,0,0,Bu,0,B,[],3,3,0,0,0,BD,0,B,[],3,3,0,0,0,BC,0,B,[Bu,BD],0,3,0,0,0,Ce,0,B,[],4,0,0,0,0,Cp,0,B,[],4,3,0,0,0,P,0,B,[],0,3,0,0,0,Br,0,P,[],0,3,0,0,0,I,0,Br,[],0,3,0,0,0,Cv,0,I,[],0,3,0,0,0,V,0,B,[],3,3,0,0,0,Y,0,B,[],3,3,0,0,0,Bi,0,B,[],3,3,0,0,0,N,0,B,[V,Y,Bi],0,3,0,Cm,0,Bj,0,P,[],0,3,0,0,0,S,0,Bj,[],0,3,0,0,0,B6,0,S,[],0,3,0,0,0,Be,0,B,[V],1,3,0,0,0,BO,0,Be,[Y],0,3,0,0,0,Bl,0,B,[V,Bi],0,0,0,0,0,B1,0,B,[],3,3,0,0,0,BM,0,Bl,[B1],0,3,0,0,0,R,0,S,[],0,3,0,0,0,Cs,
0,R,[],0,3,0,0,0,Cd,0,R,[],0,3,0,0,0,H,0,B,[],3,3,0,0,0,Bw,0,B,[H],3,3,0,0,0,BA,0,B,[Bw],3,3,0,0,0,K,0,B,[H],3,3,0,0,0,B3,0,B,[BA,K],3,3,0,0,0,Cc,0,B,[],4,3,0,0,0,Cr,0,B,[],4,3,0,0,0,BS,0,B,[K],3,3,0,0,0,BP,0,B,[K],3,3,0,0,0,BF,0,B,[K],3,3,0,0,0,BN,0,B,[K],3,3,0,0,0,BJ,0,B,[K],3,3,0,0,0,Bv,0,B,[K,BS,BP,BF,BN,BJ],3,3,0,0,0,BX,0,B,[],3,3,0,0,0,By,0,B,[H],3,3,0,0,0,Cf,0,B,[H,Bv,BX,By],1,3,0,0,["D",Du(Cy),"bc",Dv(CK),"F",Dv(CY),"U",Dw(CW),"N",Du(CA),"W",Dt(Cz),"V",Dw(CI)],BT,0,B,[],3,3,0,0,0,B0,0,B,[BT],0,3,0,0,
0,T,0,I,[],0,3,0,0,0,B7,0,B,[],4,3,0,0,0,BI,0,I,[],0,3,0,0,0,Z,0,I,[],0,3,0,0,0,Bb,0,B,[Y],0,3,0,0,0,J,0,B,[],4,3,0,Bk,0]);
$rt_metadata([Ck,0,B,[H],1,3,0,0,0,B$,0,B,[H],1,3,0,0,0,Cq,0,B,[H],1,3,0,0,0,Bh,0,B,[H],3,3,0,0,0,BW,0,B,[Bh],0,3,0,0,["q",Du(CO)],B4,0,B,[H],1,3,0,0,0,BV,0,B,[Bh],0,3,0,0,["q",Du(CL)],Cw,0,B,[],4,3,0,0,0,Bo,0,T,[],0,3,0,0,0,Ca,0,B,[],4,3,0,0,0,Cx,0,B,[],0,3,0,0,0]);
function $rt_array(cls,data){this.br=null;this.$id$=0;this.type=cls;this.data=data;this.constructor=$rt_arraycls(cls);}$rt_array.prototype=$rt_globals.Object.create(($rt_objcls()).prototype);$rt_array.prototype.toString=function(){var str="[";for(var i=0;i<this.data.length;++i){if(i>0){str+=", ";}str+=this.data[i].toString();}str+="]";return str;};$rt_setCloneMethod($rt_array.prototype,function(){var dataCopy;if('slice' in this.data){dataCopy=this.data.slice();}else {dataCopy=new this.data.constructor(this.data.length);for
(var i=0;i<dataCopy.length;++i){dataCopy[i]=this.data[i];}}return new $rt_array(this.type,dataCopy);});$rt_stringPool(["0","<java_object>@","Either src or dest is null","null","object","function","string","number","undefined"]);
N.prototype.toString=function(){return $rt_ustr(this);};
N.prototype.valueOf=N.prototype.toString;B.prototype.toString=function(){return $rt_ustr(CR(this));};
B.prototype.__teavm_class__=function(){return $dbg_class(this);};
function $rt_startThread(runner,callback){var result;try {result=runner();}catch(e){result=e;}if(typeof callback!=='undefined'){callback(result);}else if(result instanceof $rt_globals.Error){throw result;}}function $rt_suspending(){return false;}function $rt_resuming(){return false;}function $rt_nativeThread(){return null;}function $rt_invalidPointer(){}$rt_exports.main=$rt_mainStarter(Da);
$rt_exports.main.javaException=$rt_javaException;
let Ef=$rt_globals.Symbol('jsoClass');
(function(){var c;c=Cf.prototype;c.removeEventListener=c.U;c.dispatchEvent=c.N;c.get=c.D;c.addEventListener=c.V;Object.defineProperty(c,"length",{get:c.W});c=BW.prototype;c[Ef]=true;c.accept=c.q;c=BV.prototype;c[Ef]=true;c.accept=c.q;})();
}));

//# sourceMappingURL=classes.js.map