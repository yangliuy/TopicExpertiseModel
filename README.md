TopicExpertiseModel
===================

/**
Copyright (C) 2013 by
SMU Text Mining Group/Singapore Management University/Peking University

TopicExpertiseModel is distributed for research purpose, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

If you use this code, please cite the following paper:

Liu Yang, Minghui Qiu, Swapna  Gottipati, Feida Zhu, Jing Jiang, Huiping Sun and Zhong Chen. CQARank: Jointly Model Topics and Expertise in Community Question Answering. In Proceedings of the 22nd ACM International Conference on Information and Knowledge Management (CIKM 2013).  (http://dl.acm.org/citation.cfm?id=2505720)

Feel free to contact the following people if you find any
problems in the package.
lyang@cs.umass.edu or yangliuyx@gmail.com * */

Brief Introduction
===================

1. Community Question Answering (CQA) websites, where people share expertise on open platforms, have become large repositories of valuable knowledge. To bring the best value out of these knowledge repositories, it is critically important for CQA services to know how to find the right experts, retrieve archived similar questions and recommend best answers to new questions. To tackle this cluster of closely related problems in a principled approach, we proposed Topic Expertise Model (TEM), a novel probabilistic generative model with GMM hybrid, to jointly model topics and expertise by integrating textual content model and link structure analysis. Based on TEM results, we proposed CQARank to measure user interests and expertise score under different topics. Leveraging the question answering history based on long-term community reviews and voting, our method could find experts with both similar topical preference and high topical expertise.

2. This package implements Gibbs sampling for Topic Expertise Model for jointly modeling topics and expertise in question answering communities. More details of our model are described in the following paper:

   Liu Yang, Minghui Qiu, Swapna  Gottipati, Feida Zhu, Jing Jiang, Huiping Sun and Zhong Chen. CQARank: Jointly Model Topics and Expertise in Community Question Answering. In Proceedings of the 22nd ACM International Conference on Information and Knowledge Management (CIKM 2013).  (http://dl.acm.org/citation.cfm?id=2505720)

3. I didn't upload the data under ./data folder since the total size is too large. But I upload some used experimental data into a dropbox folder. You can find the experimental data here. [Download](https://www.dropbox.com/sh/42vei96g0vf56dy/AAATUsvDMq7uXkkPsDF87K5pa?dl=0).

4. I am happy that many readers sent emails to me on questions about the paper and code since the paper was published. I am always trying my best to reply to those emails. My latest email address is lyang@cs.umass.edu / yangliuyx@gmail.com. You can also use the "Issues" function in Github so that there are QA threads which can be referred to by future readers.
