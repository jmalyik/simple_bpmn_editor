# simple_bpmn_editor

This is an embeddable BPMN editor from activiti explorer.

It works on its own, loads and saves the bpmn xml to filesystem - the main goal was when I created it that I needed an editor without a running activiti engine and be able to use it somewhere else.

## Build & run

-- Clone it.
-- Build it with maven + Java7+ (tested with java7 and java8)
-- Drop it into a tomcat

You can easily customize where the bpmn.xml will be saved, check the two Spring controller's code.
