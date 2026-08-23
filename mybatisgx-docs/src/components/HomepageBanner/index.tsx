import type {ReactNode} from 'react';
import Link from '@docusaurus/Link';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

// 发版时记得同步更新这里的版本号与 CHANGELOG 链接
const LATEST_VERSION = '0.3.0';
const RELEASE_NOTES_URL = 'https://github.com/cris-xue/mybatisgx/blob/master/CHANGELOG.md';

const databases = [
  'MySQL',
  'MariaDB',
  'OceanBase MySQL',
  'SinoDB',
  'Oracle',
  'Dameng',
  'UXDB',
  'OceanBase',
  'PostgreSQL',
  'GaussDB',
  'Vastbase',
  'Kingbase',
  'GBase',
];

export default function HomepageBanner(): ReactNode {
  return (
    <section className={styles.banner}>
      <div className="container">
        <div className={styles.bannerRow}>
          <span className={styles.version}>
            ⚡ 最新版本{' '}
            <Link to={RELEASE_NOTES_URL} className={styles.versionLink}>
              {LATEST_VERSION}
            </Link>
          </span>
          <span className={styles.databases}>🌍 支持 {databases.length} 种数据库</span>
        </div>
        <p className={styles.dbList}>{databases.join(' · ')}</p>
      </div>
    </section>
  );
}
