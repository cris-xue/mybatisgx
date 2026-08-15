import type {ReactNode} from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  description: ReactNode;
  href?: string;
};

const FeatureRow1: FeatureItem[] = [
  {
    title: '🔌 生态兼容',
    description: (
      <>
        基于 MyBatis <code>LanguageDriver</code> 实现，只要是 MyBatis 生态都能用
        MGXSQL：MyBatis / MyBatis-Plus / MyBatis-Flex，0 成本接入，无需迁移。
      </>
    ),
  },
  {
    title: '💬 MGXQL',
    description: (
      <>
        面向查询的 DSL，复杂查询用 SQL 的方式表达：
        <br />
        <code>select * from user where #[and age &gt; :age]</code>
      </>
    ),
    href: '/docs/query-language/mgxql',
  },
  {
    title: '🎯 DAO 层收敛',
    description: (
      <>
        持久层逻辑归于 DAO：方法名派生 SQL + 查询实体，Service 只表达业务意图。
        <br />
        <code>findByNameLikeAndAgeGt(...)</code>
      </>
    ),
  },
];

const FeatureRow2: FeatureItem[] = [
  {
    title: '⚡ MGXSQL',
    description: (
      <>
        动态 SQL 模板，动态条件直接写在 SQL 里：
        <br />
        <code>#[and name = :name]</code>
        <br />
        <code>#if(status == 2)[and status = :status]</code>
      </>
    ),
    href: '/docs/query-language/mgxsql',
  },
  {
    title: '🔧 低接管成本',
    description: (
      <>
        性能优化时 XML 直接覆盖，Service 层代码零改动，DAO 接口签名不变。从自动
        生成到手写 SQL，是一条连续自然的路。
      </>
    ),
  },
  {
    title: '🎨 渐进式控制',
    description: (
      <>
        从零配置到完全掌控：90% 框架生成 + 9% XML 覆盖 + 1% 手写复杂 SQL。
        声明式关联查询，四种抓取模式精确控制性能。
      </>
    ),
  },
];

function Feature({title, description, href}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">
          {href ? (
            <Link className={styles.featureLink} to={href}>
              {title}
            </Link>
          ) : (
            title
          )}
        </Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

function FeatureRow({items}: {items: FeatureItem[]}) {
  return (
    <div className="row">
      {items.map((props, idx) => (
        <Feature key={idx} {...props} />
      ))}
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <FeatureRow items={FeatureRow1} />
        <FeatureRow items={FeatureRow2} />
      </div>
    </section>
  );
}
