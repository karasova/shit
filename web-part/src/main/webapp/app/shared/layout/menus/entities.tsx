import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import {DropdownItem} from 'reactstrap';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {Translate, translate} from 'react-jhipster';
import {NavLink as Link} from 'react-router-dom';
import {NavDropdown} from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    style={{maxHeight: '80vh', overflow: 'auto'}}
  >
    <MenuItem icon="asterisk" to="/team">
      <Translate contentKey="global.menu.entities.team"/>
    </MenuItem>
    <MenuItem icon="asterisk" to="/participant">
      <Translate contentKey="global.menu.entities.participant"/>
    </MenuItem>
    <MenuItem icon="asterisk" to="/track">
      <Translate contentKey="global.menu.entities.track"/>
    </MenuItem>
    <MenuItem icon="asterisk" to="/mailing-task">
      <Translate contentKey="global.menu.entities.mailingTask"/>
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
