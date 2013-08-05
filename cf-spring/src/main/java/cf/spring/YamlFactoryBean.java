package cf.spring;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

public class YamlFactoryBean extends AbstractFactoryBean<Object> {

	private Resource yamlFile;
	
	@Override
	protected Object createInstance() throws Exception {
		return new Yaml().load(yamlFile.getInputStream());
	}
	
	@Override
	public Class<?> getObjectType() {
		return Object.class;
	}
	
	public void setYamlFile(Resource yamlFile) {
		this.yamlFile = yamlFile;
	}

}
