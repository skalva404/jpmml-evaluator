/*
 * Copyright (c) 2013 Villu Ruusmann
 *
 * This file is part of JPMML-Evaluator
 *
 * JPMML-Evaluator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPMML-Evaluator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with JPMML-Evaluator.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jpmml.evaluator.tree;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.BiMap;
import org.dmg.pmml.DataType;
import org.dmg.pmml.tree.Node;
import org.jpmml.evaluator.EntityClassification;
import org.jpmml.evaluator.HasConfidence;
import org.jpmml.evaluator.HasProbability;
import org.jpmml.evaluator.Numbers;
import org.jpmml.evaluator.TypeUtil;
import org.jpmml.evaluator.Value;
import org.jpmml.evaluator.ValueMap;

public class NodeScoreDistribution<V extends Number> extends EntityClassification<Node, V> implements HasProbability, HasConfidence {

	private ValueMap<String, V> confidences = null;


	NodeScoreDistribution(BiMap<String, Node> entityRegistry, Node node){
		super(Type.PROBABILITY, entityRegistry);

		setEntity(node);
	}

	@Override
	public void computeResult(DataType dataType){
		Node node = getEntity();

		if(node.hasScore()){
			Object result = TypeUtil.parseOrCast(dataType, node.getScore());

			super.setResult(result);

			return;
		}

		super.computeResult(dataType);
	}

	@Override
	public Set<String> getCategoryValues(){

		if(isEmpty()){
			Node node = getEntity();

			return Collections.singleton(node.getScore());
		}

		return keySet();
	}

	@Override
	public Double getProbability(String category){

		if(isEmpty()){
			Node node = getEntity();

			if(category != null && (category).equals(node.getScore())){
				return Numbers.DOUBLE_ONE;
			}
		}

		return getValue(category);
	}

	@Override
	public Double getConfidence(String category){
		Value<V> confidence = (this.confidences != null ? this.confidences.get(category) : null);

		return Type.CONFIDENCE.getValue(confidence);
	}

	void putConfidence(String category, Value<V> confidence){

		if(this.confidences == null){
			this.confidences = new ValueMap<>();
		}

		this.confidences.put(category, confidence);
	}

	@Override
	protected boolean isEmpty(){
		return super.isEmpty();
	}

	@Override
	protected ToStringHelper toStringHelper(){
		ToStringHelper helper = super.toStringHelper()
			.add(Type.CONFIDENCE.entryKey(), this.confidences != null ? this.confidences.entrySet() : Collections.emptySet());

		return helper;
	}
}