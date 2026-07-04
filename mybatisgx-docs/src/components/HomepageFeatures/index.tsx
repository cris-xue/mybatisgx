import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  // Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: '🎯 DAO 层收敛',
    // Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        持久层逻辑不泄露到 Service 层，业务代码更纯粹。
        方法名语义化查询，Service 层只表达业务意图，
        告别 Wrapper 地狱。
      </>
    ),
  },
  {
    title: '🔧 低接管成本',
    // Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        SQL 预生成可审查，性能优化时 XML 直接覆盖。
        Service 层代码零改动，DAO 接口签名不变，
        从自动生成到手写 SQL 是连续自然的。
      </>
    ),
  },
  {
    title: '🎨 渐进式控制',
    // Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        从零配置到完全掌控：90% 用框架生成，9% 用 XML 覆盖，1% 手写复杂 SQL。
        声明式关联查询，四种抓取模式精确控制性能。
      </>
    ),
  },
];

function Feature({title, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        {/*<Svg className={styles.featureSvg} role="img" />*/}
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
