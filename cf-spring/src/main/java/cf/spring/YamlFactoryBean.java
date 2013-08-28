package cf.spring;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

public class YamlFactoryBean extends AbstractFactoryBean<YamlDocument> {

	private Resource yamlFile;
	
	@Override
	protected YamlDocument createInstance() throws Exception {
		return YamlDocument.load(yamlFile);
	}
	
	@Override
	public Class<YamlDocument> getObjectType() {
		return YamlDocument.class;
	}
	
	public void setYamlFile(Resource yamlFile) {
		this.yamlFile = yamlFile;
	}

}
