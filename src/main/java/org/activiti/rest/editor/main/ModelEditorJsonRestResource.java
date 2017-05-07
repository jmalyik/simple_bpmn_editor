package org.activiti.rest.editor.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.impl.persistence.entity.ModelEntity;
import org.activiti.engine.impl.persistence.entity.ModelEntityManager;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Tijs Rademakers
 */
@RestController
public class ModelEditorJsonRestResource implements ModelDataJsonConstants {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);

	private final ModelEntityManager manager = new ModelEntityManager();
	@Autowired
	private ObjectMapper objectMapper;

	@RequestMapping(value = "/model/{modelId}/json", method = RequestMethod.GET, produces = "application/json")
	public ObjectNode getEditorJson(@PathVariable final String modelId) {
		ObjectNode modelNode = null;

		final Model model = createNewModel();//repositoryService.getModel(modelId);

		if (model != null) {
			try {
				if (StringUtils.isNotEmpty(model.getMetaInfo())) {
					modelNode = (ObjectNode) this.objectMapper.readTree(model.getMetaInfo());
				} else {
					modelNode = this.objectMapper.createObjectNode();
					modelNode.put(MODEL_NAME, model.getName());
				}
				modelNode.put(MODEL_ID, model.getId());
				//        ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
				//            new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));
				final ObjectNode editorJsonNode = createNewEditorJsonNode();
				modelNode.put("model", editorJsonNode);

			} catch (final Exception e) {
				LOGGER.error("Error creating model JSON", e);
				//throw new ActivitiException("Error creating model JSON", e);
			}
		}
		return modelNode;
	}

	private ObjectNode createNewEditorJsonNode() throws IOException, XMLStreamException {
		final File file = new File(System.getProperty("java.io.tmpdir"), "bpmn20.xml");
		if (file.exists()) {
			final FileInputStream fis = new FileInputStream(file);
			final BpmnXMLConverter converter = new BpmnXMLConverter();
			final XMLInputFactory xif = XMLInputFactory.newInstance();
			final InputStreamReader in = new InputStreamReader(fis, "UTF-8");
			final XMLStreamReader xtr = xif.createXMLStreamReader(in);
			final BpmnModel bpmnModel = converter.convertToBpmnModel(xtr);
			final BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			return jsonConverter.convertToJson(bpmnModel);
		} else {
			final ObjectMapper objectMapper = new ObjectMapper();
			final ObjectNode editorNode = objectMapper.createObjectNode();
			editorNode.put("id", "canvas");
			editorNode.put("resourceId", "canvas");
			final ObjectNode stencilSetNode = objectMapper.createObjectNode();
			stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			editorNode.put("stencilset", stencilSetNode);
			return editorNode;
		}
	}

	private Model createNewModel() {
		// the model id comes from the activiti database - here it is mocked (there is no database behind it) 
		// so we need to create one (it is necessary at saving)
		final Model modelData = this.manager.createNewModel();
		((ModelEntity) modelData).setId("1");
		final String name = "BPMN process";// TODO: read it from an input box
		final ObjectNode modelObjectNode = this.objectMapper.createObjectNode();
		modelObjectNode.put(MODEL_NAME, name);
		modelObjectNode.put(MODEL_REVISION, 1);
		final String description = "";// TODO: read it from an input box
		modelObjectNode.put(MODEL_DESCRIPTION, description);
		modelData.setMetaInfo(modelObjectNode.toString());
		modelData.setName(name);
		return modelData;
	}
}
