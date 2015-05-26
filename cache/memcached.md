### Memcached

- [²Î¿¼µØÖ·,Ðè·­Ç½](https://code.google.com/p/spymemcached/wiki/SpringIntegration)

```
	<!-- for memcahced client -->
	<bean id="memcachedClient" class="net.spy.memcached.spring.MemcachedClientFactoryBean">
		<property name="servers" value="${memcached.servers}" />
		<property name="protocol" value="${memcached.protocol}" />
		<property name="transcoder">
			<bean class="net.spy.memcached.transcoders.SerializingTranscoder">
				<property name="compressionThreshold" value="${memcached.compressionThreshold}" />
			</bean>
		</property>
		<!-- should server support SASL <property name="authDescriptor"> <bean 
			class="net.spy.memcached.auth.AuthDescriptor"> <constructor-arg index="0" 
			value="PLAIN" /> <constructor-arg index="1"> <bean class="net.spy.memcached.auth.PlainCallbackHandler"> 
			<constructor-arg index="0" value="${memcached.username}"/> <constructor-arg 
			index="1" value="${memcached.password}"/> </bean> </constructor-arg> </bean> 
			</property> -->
		<property name="opTimeout" value="${memcached.opTimeout}" />
		<property name="timeoutExceptionThreshold" value="${timeoutExceptionThreshold}" />
		<property name="hashAlg" value="${memcached.hashAlg}" />
		<property name="locatorType" value="${locatorType}" />
		<property name="failureMode" value="${failureMode}" />
		<property name="useNagleAlgorithm" value="${useNagleAlgorithm}" />
	</bean>
	
#####for spy memcached cliet######

###Memcached

##Need to Ckeck
memcached.servers=192.168.1.50:11211
memcached.username=dragon
memcached.password=123456
##End Check
memcached.protocol=BINARY
##transcoder
memcached.compressionThreshold=1024
memcached.opTimeout=2000
timeoutExceptionThreshold=1998
memcached.hashAlg=KETAMA_HASH
locatorType=CONSISTENT
failureMode=Redistribute
useNagleAlgorithm=false

```
